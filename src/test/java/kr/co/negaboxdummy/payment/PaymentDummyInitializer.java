package kr.co.negaboxdummy.payment;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.payment.model.OrderGetRes;
import kr.co.negaboxdummy.payment.model.PaymentPostReq;
import kr.co.negaboxdummy.payment.model.ReservationGetRes;
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

        for (ReservationGetRes reservation : reservationList) {
            int paymentMethod = (int)(Math.random() * 3);
            int status = reservation.getStatus() == 1 ? 1 : 2;

            PaymentPostReq req = PaymentPostReq.builder()
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

            paymentMapper.save(req);
        }

        sqlSession.flushStatements();

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

            paymentMapper.save(req);
        }

        sqlSession.flushStatements();

        sqlSession.close();
    }
}
