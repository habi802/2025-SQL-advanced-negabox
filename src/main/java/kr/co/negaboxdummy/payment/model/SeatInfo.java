package kr.co.negaboxdummy.payment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SeatInfo {
    private long seatId;
    private long screenId;
    private String rowLabel;
    private int colNo;
}
