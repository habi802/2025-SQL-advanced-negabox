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
public class ReservationReq {
    private long reservationId;
    private long scheduleId;
    private Long userId;
    private Long nonUserId;
    private double price;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
