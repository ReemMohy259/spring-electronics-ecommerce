package com.electronics.dto.cart;

public record AddToCartRequest(
    Integer productId,
    Integer quantity
) {
}
