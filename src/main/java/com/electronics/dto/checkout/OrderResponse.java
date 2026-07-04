package com.electronics.dto.checkout;

import com.electronics.entity.Order;
import com.electronics.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(Integer orderId, OrderStatus status, BigDecimal totalPrice,
    OffsetDateTime timestamp, String paymentIntentId, List<OrderItemResponse> items) {
    public static OrderResponse fromEntity(Order order, String paymentIntentId) {
        return new OrderResponse(
            order.getId(),
            order.getStatus(),
            order.getTotalPrice(),
            order.getTimestamp(),
            paymentIntentId,
            order.getOrderItems().stream().map(OrderItemResponse::fromEntity).toList());
    }
}
