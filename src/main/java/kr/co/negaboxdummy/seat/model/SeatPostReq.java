package kr.co.negaboxdummy.seat.model;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class SeatPostReq {
    private long screenId;
    private char rowLabel;
    private String colNo;
    private int type;
}
