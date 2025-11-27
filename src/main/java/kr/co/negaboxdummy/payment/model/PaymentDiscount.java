package kr.co.negaboxdummy.payment.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class PaymentDiscount {
    private long paymentId;
    private long policyId;
    private long appliedAmount;
    private LocalDateTime createdAt;
}
