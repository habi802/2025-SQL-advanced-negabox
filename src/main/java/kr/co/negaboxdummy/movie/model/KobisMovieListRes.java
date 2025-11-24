package kr.co.negaboxdummy.movie.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class KobisMovieListRes {
    private MovieListResult movieListResult;

    public static class MovieListResult {
        private List<Movie> movieList;

        public static class Movie {
            private String movieCd; // 영화 코드(영화 정보 가져오는 API 호출하기 위해 필요)
            private String movieNm; // 영화 제목
            private String openDt; // 개봉일
            private String genreAlt; // 장르
            private String repNationNm; // 나라
            private List<Director> directors; // 감독 정보

            // 감독
            public static class Director {
                private String peopleNm;
            }
        }
    }
}
