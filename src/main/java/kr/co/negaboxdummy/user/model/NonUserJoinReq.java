package kr.co.negaboxdummy.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class NonUserJoinReq {
    private long nonUserId;
    private String name;
    private String phone;
    private String birth;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime expireAt;
}
