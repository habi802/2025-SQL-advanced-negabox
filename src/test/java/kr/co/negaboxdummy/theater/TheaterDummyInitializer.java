package kr.co.negaboxdummy.theater;

import kr.co.negaboxdummy.config.FakerConfig;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

public class TheaterDummyInitializer extends FakerConfig {
    @Test
    void TheaterDummyInitializer() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
    }
}
