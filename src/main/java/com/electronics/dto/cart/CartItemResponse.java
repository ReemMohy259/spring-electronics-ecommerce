package com.electronics.dto.cart;

import com.electronics.entity.CartItem;

import java.math.BigDecimal;

public record CartItemResponse(
    Integer cartItemId,
    Integer productId,
    String productName,
    String imageUrl,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal
) {

    public static CartItemResponse fromEntity(CartItem item) {
        return new CartItemResponse(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getProduct().getImageUrl(),
            item.getProduct().getPrice(),
            item.getQuantity(),
            item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}
