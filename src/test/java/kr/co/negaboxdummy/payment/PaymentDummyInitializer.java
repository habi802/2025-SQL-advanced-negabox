package kr.co.negaboxdummy.payment;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.payment.model.*;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public class PaymentDummyInitializer extends FakerConfig {
    @Test
    @Rollback(false)
    @Transactional
    void insertPayment() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        PaymentMapper paymentMapper = sqlSession.getMapper(PaymentMapper.class);

        int batchSize = 10_000;
        int offset = 0;

        // 예매 결제 등록(Out of memory 문제로 10,000개 씩 끊어서 조회해서 더미 데이터를 넣기로 함)
        while(true) {
            List<ReservationGetRes> reservationList = paymentMapper.findInReservation(batchSize, offset);
            if (reservationList.isEmpty()) {
                break;
            }

            // 예매 결제 등록
            for (ReservationGetRes reservation : reservationList) {
                System.out.println("reservation: " + reservation.getReservationId());
                if (reservation.getUserId() != null || reservation.getNonUserId() != null) {
                    int paymentMethod = (int)(Math.random() * 3);
                    int status = reservation.getStatus() == 1 ? 1 : 2;

                    List<TicketDiscountGetRes> TicketDiscountList = paymentMapper.findInTicketDiscount(reservation.getReservationId());
                    BigDecimal discountTotal = BigDecimal.ZERO;
                    for (TicketDiscountGetRes ticketDiscount : TicketDiscountList) {
                        discountTotal = discountTotal.add(ticketDiscount.getAppliedAmount());
                    }

                    BigDecimal totalAmount = reservation.getPrice().subtract(discountTotal);

                    // 카드 결제인 경우 카드사에 맞는 할인 정책 찾아 할인 금액 조회
                    String[] cardCompanyCode = {"00501", "00502", "00503", "00504", "00505", "00506", "00507", "00508", "00509", "00510", "00511", "00512"};
                    int getCardCompanyCodeIndex = (int)(Math.random() * cardCompanyCode.length);
                    DiscountPolicyGetRes discountPolicy = null;
                    BigDecimal paymentDiscountAmount = BigDecimal.ZERO;
                    if (paymentMethod == 0) {
                        discountPolicy = paymentMapper.findInDiscountPolicy(cardCompanyCode[getCardCompanyCodeIndex]);

                        if (discountPolicy != null) {
                            if (discountPolicy.getDiscountAmount() != null) {
                                // 적용되는 할인이 현금 단위인 경우
                                // 결제 금액이 최소 주문 금액 이상일 때만 할인 적용
                                if (totalAmount.compareTo(discountPolicy.getMinPrice()) >= 0) {
                                    paymentDiscountAmount = discountPolicy.getDiscountAmount();
                                    totalAmount = totalAmount.subtract(paymentDiscountAmount);
                                }
                            } else {
                                // 적용되는 할인이 퍼센트 단위인 경우
                                // 결제 금액이 최소 주문 금액 이상일 때만 할인 적용
                                // 적용될 할인 금액이 최대 할인 금액을 초과하면 최대 할인 금액만 적용
                                if (totalAmount.compareTo(discountPolicy.getMinPrice()) >= 0) {
                                    if (totalAmount.multiply(discountPolicy.getDiscountPercent().multiply(BigDecimal.valueOf(0.01))).compareTo(discountPolicy.getMaxBenefitAmount()) <= 0) {
                                        paymentDiscountAmount = totalAmount.multiply(discountPolicy.getDiscountPercent().multiply(BigDecimal.valueOf(0.01)));
                                        totalAmount = totalAmount.subtract(totalAmount.multiply(discountPolicy.getDiscountPercent().multiply(BigDecimal.valueOf(0.01))));
                                    } else {
                                        paymentDiscountAmount = discountPolicy.getMaxBenefitAmount();
                                        totalAmount = totalAmount.subtract(paymentDiscountAmount);
                                    }
                                }
                            }
                        }
                    }

                    PaymentPostReq paymentReq = PaymentPostReq.builder()
                                                              .paymentType(0)
                                                              .typeId(reservation.getReservationId())
                                                              .paymentMethod(paymentMethod)
                                                              .originAmount(reservation.getPrice())
                                                              .discountTotal(discountTotal)
                                                              .amount(totalAmount)
                                                              .status(status)
                                                              .completedAt(reservation.getCreatedAt())
                                                              .cancelledAt(reservation.getUpdatedAt())
                                                              .build();

                    paymentMapper.savePayment(paymentReq);

                    sqlSession.commit();

                    Long paymentId = paymentReq.getPaymentId();

                    // (회원만)결제 등록 후 좌석 단위 할인에 대한 등록 또는 수정
                    if (reservation.getUserId() != null) {
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
                                                                             .changeAmount(appliedAmount.negate())
                                                                             .balanceAfter(point)
                                                                             .status(1)
                                                                             .build();

                                paymentMapper.savePointLog(pointLogReq);

                                // 예매 상태가 취소인 경우 취소된 내역까지 등록
                                if (status == 2) {
                                    point = point.add(appliedAmount);

                                    pointLogReq = PointLogPostReq.builder()
                                                                 .userId(userId)
                                                                 .paymentId(paymentId)
                                                                 .changeAmount(appliedAmount)
                                                                 .balanceAfter(point)
                                                                 .status(0)
                                                                 .build();

                                    paymentMapper.savePointLog(pointLogReq);
                                }
                            } else if ("01102".equals(benefitCode)) {
                                // 쿠폰 상태 변경
                                UserCouponUpdateReq couponReq = UserCouponUpdateReq.builder()
                                                                                   .status(1)
                                                                                   .useAt(reservation.getCreatedAt())
                                                                                   .userCouponId(ticketDiscount.getBenefitId())
                                                                                   .build();

                                paymentMapper.updateUserCoupon(couponReq);

                                // 예매 상태가 취소인 경우 취소된 내역까지 등록
                                if (status == 2) {
                                    couponReq = UserCouponUpdateReq.builder()
                                                                   .status(3)
                                                                   .useAt(reservation.getCreatedAt())
                                                                   .userCouponId(ticketDiscount.getBenefitId())
                                                                   .build();

                                    paymentMapper.updateUserCoupon(couponReq);
                                }
                            } else if ("01103".equals(benefitCode)) {
                                // 교환권 상태 변경
                                UserVoucherUpdateReq voucherReq = UserVoucherUpdateReq.builder()
                                                                                      .status(1)
                                                                                      .useAt(reservation.getCreatedAt())
                                                                                      .userVoucherId(ticketDiscount.getBenefitId())
                                                                                      .build();

                                paymentMapper.updateUserVoucher(voucherReq);

                                // 예매 상태가 취소인 경우 취소된 내역까지 등록
                                if (status == 2) {
                                    voucherReq = UserVoucherUpdateReq.builder()
                                                                     .status(3)
                                                                     .useAt(reservation.getCreatedAt())
                                                                     .userVoucherId(ticketDiscount.getBenefitId())
                                                                     .build();

                                    paymentMapper.updateUserVoucher(voucherReq);
                                }
                            }
                        }
                    }

                    // 결제 수단(카드 결제, 계좌 이체, 휴대폰 결제) 내역 등록
                    if (paymentMethod == 0) {
                        // 카드 결제
                        int installmentMonths;
                        if (reservation.getPrice().subtract(discountTotal).compareTo(BigDecimal.valueOf(50_000)) >= 0) {
                            installmentMonths = 3;
                        } else {
                            installmentMonths = 0;
                        }

                        String cardNumber = faker.number().digits(4);
                        String cardApprovalNumber = faker.number().digits(6);

                        PaymentCardPostReq cardReq = PaymentCardPostReq.builder()
                                                                       .paymentId(paymentId)
                                                                       .cardCompanyCode(cardCompanyCode[getCardCompanyCodeIndex])
                                                                       .cardNumber(cardNumber)
                                                                       .installmentMonths(installmentMonths)
                                                                       .cardApprovalNumber(cardApprovalNumber)
                                                                       .build();

                        paymentMapper.savePaymentCard(cardReq);

                        // 등록된 할인 정책에 맞는 카드사가 있을 경우 결제 단위 할인 내역 등록
                        if (discountPolicy != null) {
                            PaymentDiscountPostReq paymentDiscountReq = PaymentDiscountPostReq.builder()
                                                                                              .paymentId(paymentId)
                                                                                              .policyId(discountPolicy.getPolicyId())
                                                                                              .appliedAmount(paymentDiscountAmount)
                                                                                              .build();

                            paymentMapper.savePaymentDiscount(paymentDiscountReq);
                        }
                    } else if (paymentMethod == 1) {
                        // 계좌 이체
                        String[] bankCode = {"01201", "01202", "01203", "01204", "01205", "01206", "01207", "01208", "01209", "01210", "01211", "01212", "01213", "01214"};
                        int getBankCodeIndex = (int)(Math.random() * bankCode.length);
                        String[] lastName = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "오", "한"};
                        int getLastNameIndex = (int)(Math.random() * lastName.length);
                        String[] firstName = {"지훈", "서연", "민재", "수현", "예진", "도윤", "채원", "하늘", "지우", "민서", "유진", "현우", "지민", "예린", "태윤", "세아", "시현", "준호", "아린", "시우", "소윤", "가온", "유나", "연우", "서준", "나예", "민혁", "하린", "시온", "은채"};
                        int getFirstNameIndex = (int)(Math.random() * firstName.length);

                        String accountNumber;
                        if (getBankCodeIndex == 0) {
                            accountNumber = "110-" + faker.number().digits(3) + "-" + faker.number().digits(6);
                        } else if (getBankCodeIndex == 1) {
                            accountNumber = faker.number().digits(3) + "-" + faker.number().digits(3) + "-" + faker.number().digits(4);
                        } else if (getBankCodeIndex == 2) {
                            accountNumber = "1002-" + faker.number().digits(3) + "-" + faker.number().digits(6);
                        } else if (getBankCodeIndex == 3) {
                            accountNumber = faker.number().digits(3) + "-" + faker.number().digits(6) + "-" + faker.number().digits(5);
                        } else if (getBankCodeIndex == 4) {
                            accountNumber = faker.number().digits(3) + "-" + faker.number().digits(4) + "-" + faker.number().digits(4) + "-" + faker.number().digits(2);
                        } else if (getBankCodeIndex == 5) {
                            accountNumber = faker.number().digits(3) + "-" + faker.number().digits(6) + "-" + faker.number().digits(2) + "-" + faker.number().digits(3);
                        } else if (getBankCodeIndex == 6) {
                            accountNumber = faker.number().digits(3) + "-" + faker.number().digits(2) + "-" + faker.number().digits(6);
                        } else if (getBankCodeIndex == 7) {
                            accountNumber = faker.number().digits(3) + "-" + faker.number().digits(4) + "-" + faker.number().digits(4) + "-" + faker.number().digits(3);
                        } else if (getBankCodeIndex == 8) {
                            accountNumber = "3333-" + faker.number().digits(2) + "-" + faker.number().digits(7);
                        } else {
                            accountNumber = faker.number().digits(3) + "-" + faker.number().digits(4) + "-" + faker.number().digits(6);
                        }

                        String accountHolderName = lastName[getLastNameIndex] + firstName[getFirstNameIndex];

                        PaymentBankTransferPostReq bankTransferReq = PaymentBankTransferPostReq.builder()
                                                                                               .paymentId(paymentId)
                                                                                               .bankCode(bankCode[getBankCodeIndex])
                                                                                               .accountNumber(accountNumber)
                                                                                               .accountHolderName(accountHolderName)
                                                                                               .build();

                        paymentMapper.savePaymentBankTransfer(bankTransferReq);
                    } else if (paymentMethod == 2) {
                        // 휴대폰 결제
                        String[] carrierCode = {"00901", "00902", "00903"};
                        int getCarrierCodeIndex = (int)(Math.random() * carrierCode.length);

                        String phone = "010-" + faker.number().digits(4) + "-" + faker.number().digits(4);
                        String approvalCode = faker.number().digits(10);

                        PaymentMobilePostReq mobileReq = PaymentMobilePostReq.builder()
                                                                             .paymentId(paymentId)
                                                                             .carrierCode(carrierCode[getCarrierCodeIndex])
                                                                             .phone(phone)
                                                                             .approvalCode(approvalCode)
                                                                             .build();

                        paymentMapper.savePaymentMobile(mobileReq);
                    }

                    sqlSession.flushStatements();
                }
            }

            offset += batchSize;
        }

        sqlSession.close();
    }

    @Test
    @Rollback(false)
    @Transactional
    void insertPaymentWithOrder() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        PaymentMapper paymentMapper = sqlSession.getMapper(PaymentMapper.class);

        int batchSize = 10_000;
        int offset = 0;

        // 스토어 주문 내역 결제 등록
        while(true) {
            List<OrderGetRes> orderList = paymentMapper.findInOrder(batchSize, offset);
            if (orderList.isEmpty()) {
                break;
            }

            for (OrderGetRes order : orderList) {
                System.out.println("order: " + order.getOrderId());
                int status;
                if (order.getStatus() == 0) {
                    status = 1;
                } else {
                    status = (int)(Math.random() * 2) + 2;
                }

                PaymentPostReq paymentReq = PaymentPostReq.builder()
                        .paymentType(1)
                        .typeId(order.getOrderId())
                        .paymentMethod(0)
                        .originAmount(order.getPrice())
                        .discountTotal(BigDecimal.ZERO)
                        .amount(order.getPrice())
                        .status(status)
                        .completedAt(order.getCreatedAt())
                        .cancelledAt(order.getUpdatedAt())
                        .build();

                paymentMapper.savePayment(paymentReq);

                sqlSession.commit();

                Long paymentId = paymentReq.getPaymentId();

                // 결제 등록 후 결제 수단(카드 결제) 내역 등록
                String[] cardCompanyCode = {"00501", "00502", "00503", "00504", "00505", "00506", "00507", "00508", "00509", "00510", "00511", "00512"};
                int getCardCompanyCodeIndex = (int)(Math.random() * cardCompanyCode.length);

                int installmentMonths;
                if (order.getPrice().compareTo(BigDecimal.valueOf(50_000)) >= 0) {
                    installmentMonths = 3;
                } else {
                    installmentMonths = 0;
                }

                String cardNumber = faker.number().digits(4);
                String cardApprovalNumber = faker.number().digits(6);

                PaymentCardPostReq cardReq = PaymentCardPostReq.builder()
                        .paymentId(paymentId)
                        .cardCompanyCode(cardCompanyCode[getCardCompanyCodeIndex])
                        .cardNumber(cardNumber)
                        .installmentMonths(installmentMonths)
                        .cardApprovalNumber(cardApprovalNumber)
                        .build();

                paymentMapper.savePaymentCard(cardReq);

                sqlSession.flushStatements();
            }

            offset += batchSize;
        }

        sqlSession.close();
    }
}
