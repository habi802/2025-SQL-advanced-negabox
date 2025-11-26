package kr.co.negaboxdummy.payment;

import feign.Param;
import kr.co.negaboxdummy.payment.model.ReservationCountReq;
import kr.co.negaboxdummy.payment.model.ReservationReq;
import kr.co.negaboxdummy.payment.model.ScreenReq;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PaymentMapper {
    void reservationInsert(ReservationReq reservationReq);
    void reservationCountInsert(ReservationCountReq reservationCountReq);
    ScreenReq getScreenData();

    List<Long> findScheduleIdsByDateRange(@Param("start") LocalDate start,
                                          @Param("end") LocalDate end);}
