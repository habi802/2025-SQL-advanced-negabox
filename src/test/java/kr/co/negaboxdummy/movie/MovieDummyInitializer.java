package kr.co.negaboxdummy.movie;

import kr.co.negaboxdummy.config.FakerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

public class MovieDummyInitializer extends FakerConfig {
    @Test
    @Rollback(false)
    void insertMovie() {

    }
}
