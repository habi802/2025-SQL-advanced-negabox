package kr.co.negaboxdummy.movie.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class KobisMovieInfoRes {
    private MovieInfoResult movieInfoResult;

    public static class MovieInfoResult {
        private MovieInfo movieInfo;

        public static class MovieInfo {
            private List<Actors> actors;
            private List<ShowTypes> showTypes;
            private List<Audits> audits;

            // 출연 배우
            public static class Actors {
                private String peopleNm;
            }

            // 상영관(2D, 4D, DOLBY)
            public static class ShowTypes {
                private String showTypeGroupNm;
            }

            // 검증(관람가)
            public static class Audits {
                private String watchGradeNm;
            }
        }
    }
}
