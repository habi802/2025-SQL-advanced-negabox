package kr.co.negaboxdummy.payment.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ReservationGetRes {
    private long reservationId;
    private Long userId;
    private Long nonUserId;
    private BigDecimal price;
    private int status;
    private String createdAt;
    private String updatedAt;
}
