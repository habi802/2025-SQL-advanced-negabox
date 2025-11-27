package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

@Builder
public class UserVoucherUpdateReq {
    private String useAt;
    private long userVoucherId;
}
