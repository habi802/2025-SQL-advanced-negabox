package kr.co.negaboxdummy.seat;

import kr.co.negaboxdummy.seat.model.ScreenGetRes;
import kr.co.negaboxdummy.seat.model.SeatPostReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SeatMapper {
    List<ScreenGetRes> findScreenIdAndScreenType(); // 상영관 전체 조회
    int save(SeatPostReq req); // 좌석 데이터 추가
}
