package com.electronics.dto.cart;

import java.math.BigDecimal;

public record CartItemResponse(
    Integer productId,
    String productName,
    String imageUrl,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal
) {
}
