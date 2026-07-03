package com.electronics.dto.order;

import com.electronics.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderDetailsResponse(Integer orderId, OrderStatus status, BigDecimal totalPrice,
    OffsetDateTime timestamp, String paymentIntentId, List<OrderItemResponse> items) {
}
