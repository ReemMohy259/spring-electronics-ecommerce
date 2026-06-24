package com.electronics.dto.checkout;

import com.electronics.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(Integer itemId, Integer productId, String productName,
    String imageUrl, Integer quantity, BigDecimal priceAtPurchase, BigDecimal subtotal) {
    public static OrderItemResponse fromEntity(OrderItem item) {
        return new OrderItemResponse(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getProduct().getImageUrl(),
            item.getQuantity(),
            item.getCurrentPrice(),
            item.getCurrentPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    }

}
