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
public class Payment {
    private long paymentId;
    private int paymentType;
    private long typeId;
    private int paymentMethod;
    private double originAmount;
    private double discountTotal;
    private double amount;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime canceledAt;
}
