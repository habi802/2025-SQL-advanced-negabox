package kr.co.negaboxdummy.user;

import kr.co.negaboxdummy.user.model.UserJoinReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void userJoin(UserJoinReq userJoinReq);
}
