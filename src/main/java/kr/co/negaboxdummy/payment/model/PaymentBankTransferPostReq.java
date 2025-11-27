package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

@Builder
public class PaymentBankTransferPostReq {
    private long paymentId;
    private String bankCode;
    private String accountNumber;
    private String accountHolderName;
}
