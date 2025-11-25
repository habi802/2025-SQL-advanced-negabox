package kr.co.negaboxdummy.event;

import kr.co.negaboxdummy.event.model.EventInsertReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventMapper {
    void eventInsert(EventInsertReq eventInsertReq);
}
