package kr.co.negaboxdummy.payment;

import feign.Param;
import kr.co.negaboxdummy.payment.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaymentMapper {
    void reservationInsert(ReservationReq reservationReq);
    void reservationCountInsert(ReservationCountReq reservationCountReq);
    void insertReservationSeat(ReservationSeatReq req);
    void insertReservationSeatList(ReservationSeatListReq req);

    List<Long> findScheduleIdsByDateRange(@Param("start") LocalDate start,
                                          @Param("end") LocalDate end);

    // 기간 내 상영일정 + 상영관 정보
    List<ScheduleInfo> findSchedulesWithScreenByDateRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // 상영관별 좌석 목록 (row_label, col_no 기준 정렬)
    List<SeatInfo> findSeatsByScreenId(@Param("screenId") Long screenId);

    // 스케줄별 예매 + 인원수
    List<ReservationPerson> findReservationsWithPersonCountBySchedule(
            @Param("scheduleId") Long scheduleId
    );

    LocalDateTime findReservationCreatedAt(@Param("reservationId") Long reservationId);

    List<ReservationGetRes> findInReservation();
    List<OrderGetRes> findInOrder();
    List<TicketDiscountGetRes> findInTicketDiscount(long reservationId);
    BigDecimal findPoint(long userId);
    int savePayment(PaymentPostReq req);
    int savePointLog(PointLogPostReq req);
    int updatePointInUser(UserPointUpdateReq req);
}