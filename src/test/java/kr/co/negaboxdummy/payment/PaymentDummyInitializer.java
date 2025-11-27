package kr.co.negaboxdummy.payment;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.payment.model.OrderGetRes;
import kr.co.negaboxdummy.payment.model.ReservationGetRes;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

import java.util.List;

public class PaymentDummyInitializer extends FakerConfig {
    @Test
    @Rollback(false)
    void insertPayment() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        PaymentMapper paymentMapper = sqlSession.getMapper(PaymentMapper.class);

        List<ReservationGetRes> reservationList = paymentMapper.findInReservation();
        List<OrderGetRes> orderList = paymentMapper.findInOrder();
    }
}
