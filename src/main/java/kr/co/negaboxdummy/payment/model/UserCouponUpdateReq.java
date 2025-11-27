package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

@Builder
public class UserCouponUpdateReq {
    private int status;
    private String useAt;
    private long userCouponId;
}
