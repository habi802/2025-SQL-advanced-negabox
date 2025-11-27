package kr.co.negaboxdummy.order;

import kr.co.negaboxdummy.order.model.OrderInsertReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface OrderMapper {
    void orderInsert(OrderInsertReq orderInsertReq);

    @Select("SELECT item_limit FROM store_item WHERE store_item_id = #{storeItemId}")
    int getItemLimitById(@Param("storeItemId") long storeItemId);

    @Select("SELECT price FROM store_item WHERE store_item_id = #{storeItemId}")
    double getItemPriceById(@Param("storeItemId") long storeItemId);

    void orderInsertBatch(@Param("list") List<OrderInsertReq> list);
}

