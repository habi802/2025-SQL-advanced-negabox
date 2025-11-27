package kr.co.negaboxdummy.review;

import kr.co.negaboxdummy.review.model.ReviewInsertReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface ReviewMapper {
    void reviewInsertBatch(List<ReviewInsertReq> list);
}
