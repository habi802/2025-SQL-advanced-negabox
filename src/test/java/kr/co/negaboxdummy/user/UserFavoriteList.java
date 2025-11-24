package kr.co.negaboxdummy.user;


import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.user.model.MovieFavorite;
import kr.co.negaboxdummy.user.model.TheaterFavorite;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class UserFavoriteList extends FakerConfig {
    @Autowired
    private UserMapper userMapper;

    @Test
    @Transactional
    void insertFavoriteTheater() {
        for (int i = 0; i < 70258; i++) {
            TheaterFavorite req = makeTheaterFavorite();
            userMapper.theaterFavorite(req);
        }
    }

    @Test
    @Transactional
    void insertFavoriteMovie() {
        for (int i = 0; i < 70258; i++) {
            MovieFavorite req = makeMovieFavorite();
            userMapper.movieFavorite(req);
        }
    }

    private TheaterFavorite makeTheaterFavorite() {

        int theaterId = faker.random().nextInt(1, 117);
        int userId = faker.random().nextInt(1, 163975);
        LocalDateTime createdAt = LocalDateTime.now()
                .minusDays(faker.number().numberBetween(0, 365))
                .minusHours(faker.number().numberBetween(0, 23))
                .minusMinutes(faker.number().numberBetween(0, 59));

        return TheaterFavorite.builder()
                .userId(userId)
                .theaterId(theaterId)
                .createdAt(createdAt)
                .build();
    }

    private MovieFavorite makeMovieFavorite() {

        int movieId = faker.random().nextInt(1, 117);
        int userId = faker.random().nextInt(1, 163975);
        LocalDateTime createdAt = LocalDateTime.now()
                .minusDays(faker.number().numberBetween(0, 365))
                .minusHours(faker.number().numberBetween(0, 23))
                .minusMinutes(faker.number().numberBetween(0, 59));

        return MovieFavorite.builder()
                .userId(userId)
                .movieId(movieId)
                .createdAt(createdAt)
                .build();
    }
}
