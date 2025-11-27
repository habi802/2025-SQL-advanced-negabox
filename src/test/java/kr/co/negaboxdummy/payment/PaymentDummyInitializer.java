package kr.co.negaboxdummy.payment;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.payment.model.*;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.List;

public class PaymentDummyInitializer extends FakerConfig {
    @Test
    @Rollback(false)
    void insertPayment() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        PaymentMapper paymentMapper = sqlSession.getMapper(PaymentMapper.class);

        List<ReservationGetRes> reservationList = paymentMapper.findInReservation();
        List<OrderGetRes> orderList = paymentMapper.findInOrder();

        // 예매 결제 등록
        for (ReservationGetRes reservation : reservationList) {
            if (reservation.getUserId() != null || reservation.getNonUserId() != null) {
                int paymentMethod = (int)(Math.random() * 3);
                int status = reservation.getStatus() == 1 ? 1 : 2;

                PaymentPostReq paymentReq = PaymentPostReq.builder()
                        .paymentType(0)
                        .typeId(reservation.getReservationId())
                        .paymentMethod(paymentMethod)
                        .originAmount(BigDecimal.valueOf(0.0))
                        .discountTotal(BigDecimal.valueOf(0.0))
                        .amount(BigDecimal.valueOf(0.0))
                        .status(status)
                        .completedAt("")
                        .cancelledAt("")
                        .build();

                paymentMapper.savePayment(paymentReq);

                Long paymentId = paymentReq.getPaymentId();

                // (회원만)결제 등록 후 좌석 단위 할인에 대한 등록 또는 수정
                if (reservation.getUserId() != null) {
                    List<TicketDiscountGetRes> TicketDiscountList = paymentMapper.findInTicketDiscount(reservation.getReservationId());
                    for (TicketDiscountGetRes ticketDiscount : TicketDiscountList) {
                        String benefitCode = ticketDiscount.getBenefitCode();
                        if ("01101".equals(benefitCode)) {
                            // 포인트 사용 내역 등록
                            long userId = ticketDiscount.getBenefitId();
                            BigDecimal point = paymentMapper.findPoint(userId);
                            BigDecimal appliedAmount = ticketDiscount.getAppliedAmount();

                            point = point.subtract(appliedAmount);

                            PointLogPostReq pointLogReq = PointLogPostReq.builder()
                                                                         .userId(userId)
                                                                         .paymentId(paymentId)
                                                                         .changeAmount(appliedAmount)
                                                                         .balanceAfter(point)
                                                                         .status(1)
                                                                         .build();

                            paymentMapper.savePointLog(pointLogReq);
                        } else if ("01102".equals(benefitCode)) {
                            // 쿠폰 상태 변경
                            UserCouponUpdateReq couponReq = UserCouponUpdateReq.builder()
                                                                               .useAt("")
                                                                               .userCouponId(ticketDiscount.getBenefitId())
                                                                               .build();

                            paymentMapper.updateUserCoupon(couponReq);
                        } else if ("01103".equals(benefitCode)) {
                            // 교환권 상태 변경
                            UserVoucherUpdateReq voucherReq = UserVoucherUpdateReq.builder()
                                                                                  .useAt("")
                                                                                  .userVoucherId(ticketDiscount.getBenefitId())
                                                                                  .build();

                            paymentMapper.updateUserVoucher(voucherReq);
                        }
                    }
                }

                // 결제 단위 할인 등록


                // 결제 수단(카드 결제, 계좌 이체, 휴대폰 결제) 내역 등록


                sqlSession.flushStatements();
            }
        }

        // 스토어 주문 내역 결제 등록
        for (OrderGetRes order : orderList) {
            int status;
            if (order.getStatus() == 0) {
                status = 1;
            } else {
                status = (int)(Math.random() * 2) + 2;
            }

            PaymentPostReq req = PaymentPostReq.builder()
                                               .paymentType(1)
                                               .typeId(order.getOrderId())
                                               .paymentMethod(0)
                                               .originAmount(BigDecimal.valueOf(0.0))
                                               .discountTotal(BigDecimal.valueOf(0.0))
                                               .amount(BigDecimal.valueOf(0.0))
                                               .status(status)
                                               .completedAt("")
                                               .cancelledAt("")
                                               .build();

            paymentMapper.savePayment(req);

            // 결제 등록 후 결제 수단(카드 결제) 내역 등록


            sqlSession.flushStatements();
        }

        sqlSession.close();
    }
}
