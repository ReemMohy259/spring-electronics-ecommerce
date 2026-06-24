package com.electronics.exception.cart;

import com.electronics.exception.EcommerceException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class CartNotFoundException extends EcommerceException {

    public CartNotFoundException(Integer customerId) {
        super(
            "Cart of customer with id=%s not found".formatted(customerId),
            HttpStatus.NOT_FOUND.value(),
            "CART_NOT_FOUND",
            Map.of("customerId", customerId)
        );
    }
}