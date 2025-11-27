package kr.co.negaboxdummy.order.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class OrderInsertReq {
    private long orderId;
    private long userId;
    private long storeItemId;
    private int quantity;
    private double unitPrice;
    private double price;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
