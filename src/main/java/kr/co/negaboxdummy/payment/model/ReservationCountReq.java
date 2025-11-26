package kr.co.negaboxdummy.payment.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class ReservationCountReq {
    private Long reservationId;
    private String ageType;
    private int count;
    private double price;
}
