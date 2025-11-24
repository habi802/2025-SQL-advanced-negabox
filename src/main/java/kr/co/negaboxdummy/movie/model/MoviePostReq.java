package kr.co.negaboxdummy.movie.model;

import lombok.Builder;

@Builder
public class MoviePostReq {
    private long adminId;
    private String title;
    private String releaseDate;
    private int runningTime;
    private String classification;
    private String genre;
    private String plot;
    private String type;
    private String director;
    private String actor;
}
