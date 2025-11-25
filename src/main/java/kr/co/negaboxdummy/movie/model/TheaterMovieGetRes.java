package kr.co.negaboxdummy.movie.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TheaterMovieGetRes {
    private long screenId;
    private String screenType;
    private long movieId;
    private int runningTime;
    private long employeeId;
    private String startDate;
    private String endDate;
}
