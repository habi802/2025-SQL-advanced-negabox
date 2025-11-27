package kr.co.negaboxdummy.review.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class ReviewInsertReq {
    private long movieId;
    private long scheduleId;
    private long userId;
    private int reviewRating;
    private String reviewText;
    private int isDelete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
