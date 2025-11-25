package kr.co.negaboxdummy.movie.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
public class KobisMovieListRes {
    private MovieListResult movieListResult;

    @Getter
    public static class MovieListResult {
        private List<Movie> movieList;

        @Getter
        @ToString
        public static class Movie {
            private String movieCd; // 영화 코드(영화 정보 가져오는 API 호출하기 위해 필요)
            private String genreAlt; // 장르
            private String repNationNm; // 나라
        }
    }
}
