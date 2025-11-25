package kr.co.negaboxdummy.event.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class EventInsertReq {
    private long partId;
    private long eventId;
    private long userId;
    private LocalDateTime partDate;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
