package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CartItemNotFoundException extends EcommerceException {

    public CartItemNotFoundException(Integer productId) {
        super(
            "Product of id=%s is not in your cart".formatted(productId),
            HttpStatus.NOT_FOUND.value(),
            "CART_NOT_FOUND",
            Map.of("productId", productId)
        );
    }
}