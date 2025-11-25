package kr.co.negaboxdummy.employee;

import feign.Param;
import feign.RequestLine;
import kr.co.negaboxdummy.employee.model.KobisActorListRes;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "kobisActorList")
public interface KobisActorListFeignClient {
    // 영화인 리스트를 가져오는 API를 호출하는 메소드
    @RequestLine("GET /searchPeopleList.json?key={key}&itemPerPage=100&curPage={curPage}")
    KobisActorListRes getActorList(@Param String key,
                                   @Param String curPage);
}
