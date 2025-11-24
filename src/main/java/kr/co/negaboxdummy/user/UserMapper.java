package kr.co.negaboxdummy.user;

import kr.co.negaboxdummy.user.model.MovieFavorite;
import kr.co.negaboxdummy.user.model.NonUserJoinReq;
import kr.co.negaboxdummy.user.model.TheaterFavorite;
import kr.co.negaboxdummy.user.model.UserJoinReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void userJoin(UserJoinReq userJoinReq);
    void nonUserJoin(NonUserJoinReq nonUserJoinReq);
    void theaterFavorite(TheaterFavorite theaterFavorite);
    void movieFavorite(MovieFavorite movieFavorite);
}
