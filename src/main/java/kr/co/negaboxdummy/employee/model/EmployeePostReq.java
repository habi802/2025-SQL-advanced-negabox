package kr.co.negaboxdummy.employee.model;

import lombok.Builder;

@Builder
public class EmployeePostReq {
    private long theaterId;
    private long adminId;
    private String name;
    private String phone;
    private int type;
    private int isActive;
}
