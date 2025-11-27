package kr.co.negaboxdummy.payment.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DiscountPolicyGetRes {
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal minPrice;
    private BigDecimal maxBenefitAmount;
}
