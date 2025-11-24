package kr.co.negaboxdummy.movie;

import feign.Param;
import feign.RequestLine;
import kr.co.negaboxdummy.movie.model.KobisMovieListRes;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "kobisMovieList")
public interface KobisMovieListFeignClient {
    // 영화 리스트 가져오는 API 호출하는 메소드

    @RequestLine("GET /searchMovieList.json?key={key}&openStartDt={openStartDt}&openEndDt={openEndDt}&itemPerPage={itemPerPage}&curPage={curPage}")
    KobisMovieListRes getMovieList(@Param String key,
                                   @Param String openStartDt,
                                   @Param String openEndDt,
                                   @Param String itemPerPage,
                                   @Param String curPage);
}
