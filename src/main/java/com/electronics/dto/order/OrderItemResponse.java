package com.electronics.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(Integer itemId, Integer productId, String productName,
    String imageUrl, Integer quantity, BigDecimal priceAtPurchase, BigDecimal subtotal) {
}
