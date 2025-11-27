package kr.co.negaboxdummy.payment.model;

import lombok.Builder;

@Builder
public class UserCouponUpdateReq {
    private String useAt;
    private long userCouponId;
}
