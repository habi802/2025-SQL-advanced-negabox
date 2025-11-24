package kr.co.negaboxdummy.movie;

import feign.Param;
import feign.RequestLine;
import kr.co.negaboxdummy.movie.model.KobisMovieInfoRes;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "kobisMovieInfo")
public interface KobisMovieInfoFeignClient {
    // 영화 상세 정보 가져오는 API 호출하는 메소드
    @RequestLine("GET /searchMovieInfo.json?key={key}&movieCd={movieCd}")
    KobisMovieInfoRes getMovieInfo(@Param String key,
                                   @Param String movieCd);
}
