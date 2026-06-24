package com.electronics.dto.cart;

import com.electronics.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(Integer cartId, Integer totalQuantity, BigDecimal totalPrice,
    List<CartItemResponse> items) {

    public static CartResponse fromEntity(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems()
            .stream()
            .map(CartItemResponse::fromEntity)
            .toList();

        return new CartResponse(cart.getId(), cart.getTotalQuantity(), cart.getTotalPrice(), items);
    }
}
