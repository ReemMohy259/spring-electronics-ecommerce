package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ReviewNotFoundException extends EcommerceException {

    public ReviewNotFoundException(String email, Integer productId) {
        super(
            "User with email=%s doesnt have review on product with id=%d"
                .formatted(email, productId),
            HttpStatus.NOT_FOUND.value(), "REVIEW_NOT_FOUND",
            Map.of("email", email, "productId", productId));
    }
}
