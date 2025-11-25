package kr.co.negaboxdummy.payment;

import kr.co.negaboxdummy.payment.model.ReservationReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    void ReservationInsert(ReservationReq reservationReq);
}
