package kr.co.negaboxdummy.payment.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentPostReq {
    private Long paymentId;
    private int paymentType;
    private long typeId;
    private int paymentMethod;
    private BigDecimal originAmount;
    private BigDecimal discountTotal;
    private BigDecimal amount;
    private int status;
    private String completedAt;
    private String cancelledAt;
}
