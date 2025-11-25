package kr.co.negaboxdummy.movie;

import kr.co.negaboxdummy.movie.model.ScreenSchedulePostReq;
import kr.co.negaboxdummy.movie.model.TheaterMovieGetRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScreenScheduleMapper {
    List<TheaterMovieGetRes> findInTheaterMovie();
    int save(ScreenSchedulePostReq req);
}
