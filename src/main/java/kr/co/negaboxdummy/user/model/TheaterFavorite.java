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
public class TheaterFavorite {
    private long userId;
    private long theaterId;
    private LocalDateTime createdAt;
}
