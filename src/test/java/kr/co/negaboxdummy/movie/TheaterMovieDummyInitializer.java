package kr.co.negaboxdummy.movie;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.movie.model.MovieGetRes;
import kr.co.negaboxdummy.movie.model.TheaterGetRes;
import kr.co.negaboxdummy.movie.model.TheaterMoviePostReq;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TheaterMovieDummyInitializer extends FakerConfig {

    @Test
    @Rollback(false)
    void insertTheaterMovie() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        TheaterMovieMapper theaterMovieMapper = sqlSession.getMapper(TheaterMovieMapper.class);
        List<MovieGetRes> movies = theaterMovieMapper.findMovieIdAndReleaseDate();
        List<TheaterGetRes> theaters = theaterMovieMapper.findTheaterIdAndRegion();

        for (MovieGetRes movie : movies) {
            int adminId = (int)(Math.random() * 4) + 1;

            // 상영 종료 기간
            // 서울, 경기: 상영 시작 기간으로부터 1~3주 뒤로 설정
            // 나머지: 0~(서울, 경기 숫자)주 뒤로 설정
            int seoulWeeks = (int)(Math.random() * 4) + 1;
            int remainWeeks = (int)(Math.random() * seoulWeeks);

            for (TheaterGetRes theater : theaters) {
                long theaterId = theater.getTheaterId();
                long movieId = movie.getMovieId();
                String stratDate = movie.getReleaseDate();

                int weeks = "00801".equals(theater.getRegion()) || "00802".equals(theater.getRegion()) ? seoulWeeks : remainWeeks;
                if (weeks != 0) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate date = LocalDate.parse(stratDate, formatter);
                    LocalDate newDate = date.plusWeeks(weeks);
                    String endDate = newDate.format(formatter);

                    TheaterMoviePostReq req = TheaterMoviePostReq.builder()
                            .theaterId(theaterId)
                            .movieId(movieId)
                            .adminId(adminId)
                            .startDate(stratDate)
                            .endDate(endDate)
                            .build();

                    theaterMovieMapper.save(req);
                }
            }

            sqlSession.flushStatements();
        }
    }
}
