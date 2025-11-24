package kr.co.negaboxdummy.movie;

import kr.co.negaboxdummy.movie.model.KobisMovieInfoRes;
import kr.co.negaboxdummy.movie.model.KobisMovieListRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "kobisMovie")
public interface KobisMovieFeignClient {
    @GetMapping("https://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json")
    List<KobisMovieListRes> getMovieList();

    @GetMapping("https://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json")
    KobisMovieInfoRes getMovieInfo();
}
