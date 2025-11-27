package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

@Builder
public class PaymentMobilePostReq {
    private long paymentId;
    private String carrierCode;
    private String phone;
    private String approvalCode;
}
