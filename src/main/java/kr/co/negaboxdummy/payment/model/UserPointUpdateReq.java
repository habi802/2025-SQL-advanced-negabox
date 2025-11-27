package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class UserPointUpdateReq {
    private BigDecimal point;
    private long userId;
}
