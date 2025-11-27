package kr.co.negaboxdummy.payment.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class ReservationSeatReq {
    private long reservationSeatId;
    private long scheduleId;
    private long seatId;
    private LocalDateTime createdAt;
}
