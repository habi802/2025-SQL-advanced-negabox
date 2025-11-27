package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

@Builder
public class PaymentCardPostReq {
    private long paymentId;
    private String cardCompanyCode;
    private String cardNumber;
    private int installmentMonths;
    private String cardApprovalNumber;
}
