package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ProductReviewedException extends EcommerceException {

    public ProductReviewedException(String email, Integer productId) {
        super(
            "Product with id=%s already reviewed by user with email=%s".formatted(productId, email),
            HttpStatus.BAD_REQUEST.value(), "PRODUCT_ALREADY_REVIEWED",
            Map.of("id", productId, "email", email));
    }
}
