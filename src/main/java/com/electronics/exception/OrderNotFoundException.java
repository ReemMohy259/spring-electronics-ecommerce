package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class OrderNotFoundException extends EcommerceException {

    public OrderNotFoundException(Integer id) {
        super("Order with id=%d not found".formatted(id), HttpStatus.NOT_FOUND.value(),
            "ORDER_NOT_FOUND", Map.of("id", id));
    }
}
