package kr.co.negaboxdummy.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class UserJoinReq {
    private long userId;
    private int membershipId;
    private String name;
    private String email;
    private String password;
    private LocalDate birth;
    private String carrierCode;
    private String phone;
    private double point;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String cardNum;
    private LocalDateTime gradeUpdatedAt;
}