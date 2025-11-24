package kr.co.negaboxdummy.seat;

import kr.co.negaboxdummy.seat.model.SeatPostReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeatMapper {
    int save(SeatPostReq req);
}
