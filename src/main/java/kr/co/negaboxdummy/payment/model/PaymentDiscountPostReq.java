package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class PaymentDiscountPostReq {
    private long paymentId;
    private long policyId;
    private BigDecimal appliedAmount;
}
