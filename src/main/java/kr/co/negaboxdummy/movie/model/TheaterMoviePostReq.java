package kr.co.negaboxdummy.movie.model;

import lombok.Builder;

@Builder
public class TheaterMoviePostReq {
    private long theaterId;
    private long movieId;
    private long adminId;
    private String startDate;
    private String endDate;
}
