package kr.co.negaboxdummy.movie.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
public class KobisMovieInfoRes {
    private MovieInfoResult movieInfoResult;

    @Getter
    public static class MovieInfoResult {
        private MovieInfo movieInfo;

        @Getter
        @ToString
        public static class MovieInfo {
            private String movieNm;
            private String showTm; // 개봉일
            private String openDt; // 상영 시간
            private List<Directors> directors; // 감독
            private List<Actors> actors; // 배우
            private List<Audits> audits; // 관람 등급
            private List<ShowTypes> showTypes; // 상영관(2D, 4D, DOLBY)

            // 감독
            @Getter
            @ToString
            public static class Directors {
                private String peopleNm;
            }

            // 출연 배우
            @Getter
            @ToString
            public static class Actors {
                private String peopleNm;
            }

            // 관람 등급
            @Getter
            @ToString
            public static class Audits {
                private String watchGradeNm;
            }

            // 상영관(2D, 4D, DOLBY)
            @Getter
            @ToString
            public static class ShowTypes {
                private String showTypeGroupNm;
            }
        }
    }
}
