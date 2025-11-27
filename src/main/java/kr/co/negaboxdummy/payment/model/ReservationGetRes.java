package kr.co.negaboxdummy.payment.model;

import lombok.Getter;

@Getter
public class ReservationGetRes {
    private long reservationId;
    private Long userId;
    private Long nonUserId;
    private int price;
    private int status;
    private String updatedAt;
}
