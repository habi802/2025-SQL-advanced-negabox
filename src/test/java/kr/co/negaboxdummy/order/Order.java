package kr.co.negaboxdummy.order;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.order.model.OrderInsertReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order extends FakerConfig {

    @Autowired private OrderMapper orderMapper;

    private static final int BATCH_SIZE = 1_000;

    @Test
    @Transactional
    void orderInsert() {
        int LENGTH = 17827;
        List<OrderInsertReq> batch = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < LENGTH; i++) {
            OrderInsertReq req = makeOrder();
            batch.add(req);

            if (batch.size() == BATCH_SIZE || i == LENGTH - 1) {
                orderMapper.orderInsertBatch(batch);
                batch.clear();
            }
        }

        System.out.println("주문 더미 데이터 " + LENGTH + "건 삽입 완료");
    }

    private OrderInsertReq makeOrder() {
        long userId = faker.number().numberBetween(1, 20000);
        long storeItemId = faker.number().numberBetween(1, 38);

        int maxQty = orderMapper.getItemLimitById(storeItemId);
        int quantity = faker.number().numberBetween(1, maxQty);

        double unitPrice = orderMapper.getItemPriceById(storeItemId);
        double totalPrice = unitPrice * quantity;

        LocalDateTime orderDate = LocalDateTime.now()
                .minusDays(faker.number().numberBetween(0, 180))
                .withHour(faker.number().numberBetween(10, 23))
                .withMinute(faker.options().option(0, 15, 30, 45))
                .withSecond(0);

        return OrderInsertReq.builder()
                .userId(userId)
                .storeItemId(storeItemId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .price(totalPrice)
                .status(faker.options().option(0, 1, 2))
                .createdAt(orderDate)
                .updatedAt(orderDate.plusMinutes(faker.number().numberBetween(0, 120)))
                .build();
    }
}

