package kr.co.negaboxdummy.movie;

import kr.co.negaboxdummy.movie.model.MoviePostReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovieMapper {
    int save(MoviePostReq req);
}
