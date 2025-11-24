package kr.co.negaboxdummy.movie;

import kr.co.negaboxdummy.movie.model.MovieGetRes;
import kr.co.negaboxdummy.movie.model.TheaterGetRes;
import kr.co.negaboxdummy.movie.model.TheaterMoviePostReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TheaterMovieMapper {
    List<TheaterGetRes> findTheaterIdAndRegion();
    List<MovieGetRes> findMovieIdAndReleaseDate();
    int save(TheaterMoviePostReq req);
}
