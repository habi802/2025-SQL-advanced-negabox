package kr.co.negaboxdummy.payment.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderGetRes {
    private long orderId;
    private BigDecimal price;
    private String createdAt;
}
