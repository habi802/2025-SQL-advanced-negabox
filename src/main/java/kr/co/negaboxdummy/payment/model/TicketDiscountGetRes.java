package kr.co.negaboxdummy.payment.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TicketDiscountGetRes {
    private String benefitCode;
    private long benefitId;
    private BigDecimal appliedAmount;
}
