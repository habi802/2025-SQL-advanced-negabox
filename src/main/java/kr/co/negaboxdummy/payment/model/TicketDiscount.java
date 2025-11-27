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
public class TicketDiscount {
    private long benefitId;
    private long reservationSeatId;
    private String benefitCode;
    private double appliedAmount;
    private LocalDateTime createdAt;
}
