package kr.co.negaboxdummy.movie;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.movie.model.KobisMovieInfoRes;
import kr.co.negaboxdummy.movie.model.KobisMovieListRes;
import kr.co.negaboxdummy.movie.model.MoviePostReq;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieDummyInitializer extends FakerConfig {
    @Value("${constants.kobis-movie.key}")
    String key;

    @Test
    @Rollback(false)
    void insertMovie() {
        final String OPEN_START_DATE = "2024";
        final String OPEN_END_DATE = "2025";
        final String ITEM_PER_PAGE = "100";
        int curPage = 1;
        int size;

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        MovieMapper movieMapper = sqlSession.getMapper(MovieMapper.class);

        KobisMovieListFeignClient movieListClient = Feign.builder()
                                                         .encoder(new JacksonEncoder())
                                                         .decoder(new JacksonDecoder())
                                                         .target(KobisMovieListFeignClient.class, "https://kobis.or.kr/kobisopenapi/webservice/rest/movie");

        KobisMovieInfoFeignClient movieInfoClient = Feign.builder()
                                                         .encoder(new JacksonEncoder())
                                                         .decoder(new JacksonDecoder())
                                                         .target(KobisMovieInfoFeignClient.class, "https://www.kobis.or.kr/kobisopenapi/webservice/rest/movie");

        while(true) {
            // 영화 목록 가져오기(한 페이지당 100개까지만 불러옴)
            KobisMovieListRes movieListResult = movieListClient.getMovieList(key, OPEN_START_DATE, OPEN_END_DATE, ITEM_PER_PAGE, String.valueOf(curPage));
            //System.out.println("movie: " + movieListResult.getMovieListResult().getMovieList().get(0));

            List<KobisMovieListRes.MovieListResult.Movie> movies = movieListResult.getMovieListResult().getMovieList();
            size = movies.size();

            if (size < 100) {
                break;
            }

            for (KobisMovieListRes.MovieListResult.Movie movie : movies) {
                int adminId = (int)(Math.random() * 4) + 1;

                // 영화 1개의 상세 정보 가져오기
                // 장르에 성인물이나 에로 있으면 제외하기(-_-;)
                if (!movie.getGenreAlt().contains("성인물") && !movie.getGenreAlt().contains("에로")) {
                    KobisMovieInfoRes movieInfoResult = movieInfoClient.getMovieInfo(key, movie.getMovieCd());
                    //System.out.println("movieInfo: " + movieInfo.getMovieInfoResult().getMovieInfo());
                    if (movieInfoResult.getMovieInfoResult() != null) {
                        if (movieInfoResult.getMovieInfoResult().getMovieInfo() != null) {
                            KobisMovieInfoRes.MovieInfoResult.MovieInfo movieInfo = movieInfoResult.getMovieInfoResult().getMovieInfo();

                            String title = movieInfo.getMovieNm();
                            String genre = movie.getGenreAlt();
                            // 상영 시간 안 나온 영화도 있어서 120 ~ 180 중 랜덤으로 넣기
                            String showTm = movieInfo.getShowTm();
                            int formattedShowTm = "".equals(showTm) ? (int)(Math.random() * 61) + 120 : Integer.parseInt(showTm);
                            String openDt = movieInfo.getOpenDt();
                            // "20250101" 형태로 오기 때문에 "2025-01-01" 형태로 바꿔야 함
                            String formattedOpenDt = openDt.substring(0, 4) + "-" + openDt.substring(4, 6) + "-" + openDt.substring(6, 8);

                            // 감독 이름 리스트 가져와서 이름만 ", "로 연결해서 넣기
                            List<KobisMovieInfoRes.MovieInfoResult.MovieInfo.Directors> directors = movieInfo.getDirectors();
                            String directorName = directors.stream()
                                                           .map(KobisMovieInfoRes.MovieInfoResult.MovieInfo.Directors::getPeopleNm)
                                                           .collect(Collectors.joining(", "));

                            // 배우 이름 리스트 가져와서 이름만 ", "로 연결해서 넣기
                            List<KobisMovieInfoRes.MovieInfoResult.MovieInfo.Actors> actors = movieInfo.getActors();
                            String actorName = actors.stream()
                                                     .map(KobisMovieInfoRes.MovieInfoResult.MovieInfo.Actors::getPeopleNm)
                                                     .limit(10)
                                                     .collect(Collectors.joining(", "));

                            // 관람 등급 리스트 가져와서 하나만 넣기
                            List<KobisMovieInfoRes.MovieInfoResult.MovieInfo.Audits> audits = movieInfo.getAudits();
                            String audit = audits.stream()
                                                 .map(KobisMovieInfoRes.MovieInfoResult.MovieInfo.Audits::getWatchGradeNm)
                                                 .limit(1)
                                                 .collect(Collectors.joining(", "));

                            // 상영관 타입 리스트 가져와서 2D, 4D, DOLBY에 해당되는 것만 찾아서 넣기
                            List<KobisMovieInfoRes.MovieInfoResult.MovieInfo.ShowTypes> showTypes = movieInfo.getShowTypes();
                            Set<String> includeName = Set.of("2D", "4D", "DOLBY");
                            String showType = showTypes.stream()
                                                       .map(KobisMovieInfoRes.MovieInfoResult.MovieInfo.ShowTypes::getShowTypeGroupNm)
                                                       .map(name -> {
                                                           if (name.contains("DOLBY")) {
                                                               return "DOLBY";
                                                           } else {
                                                               return name;
                                                           }
                                                       })
                                                       .filter(includeName::contains)
                                                       .distinct()
                                                       .collect(Collectors.joining(", "));

                            // 관람 등급이 청소년관람불가이고, 장르가 로맨스인 영화(이것도 성인물임 -_-...)는 제외하기
                            if (!audit.contains("청소년") && !genre.contains("로맨스")) {
                                MoviePostReq req = MoviePostReq.builder()
                                                               .adminId(adminId)
                                                               .title(title)
                                                               .releaseDate(formattedOpenDt)
                                                               .runningTime(formattedShowTm)
                                                               .classification(audit)
                                                               .genre(genre)
                                                               .plot(faker.lorem().sentence(30))
                                                               .type(showType)
                                                               .director(directorName)
                                                               .actor(actorName)
                                                               .build();

                                movieMapper.save(req);
                            }
                        }
                    }
                }
            }

            sqlSession.flushStatements();

            curPage++;
        }

        sqlSession.close();
    }
}
