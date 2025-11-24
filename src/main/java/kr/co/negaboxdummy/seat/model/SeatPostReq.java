package kr.co.negaboxdummy.seat.model;

import lombok.Builder;

@Builder
public class SeatPostReq {
    private long screenId;
    private char rowLabel;
    private char colNo;
    private int type;
}
