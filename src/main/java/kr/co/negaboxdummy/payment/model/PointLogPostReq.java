package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class PointLogPostReq {
    private long userId;
    private long paymentId;
    private BigDecimal changeAmount;
    private BigDecimal balanceAfter;
    private int status;
}
