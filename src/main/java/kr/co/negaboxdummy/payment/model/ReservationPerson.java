package kr.co.negaboxdummy.payment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReservationPerson {
    private long reservationId;
    private long scheduleId;
    private int personCount;
}
