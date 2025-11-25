package kr.co.negaboxdummy.movie.model;

import lombok.Builder;

@Builder
public class ScreenSchedulePostReq {
    private long screenId;
    private String screenType;
    private String screenTime;
    private long movieId;
    private long employeeId;
    private String runningDate;
    private String startTime;
    private String endTime;
}
