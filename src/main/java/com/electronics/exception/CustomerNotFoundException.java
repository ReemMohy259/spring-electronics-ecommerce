package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CustomerNotFoundException extends EcommerceException {

    public CustomerNotFoundException(Integer id) {
        super(
            "Customer with id=%d not found".formatted(id),
            HttpStatus.NOT_FOUND.value(),
            "USER_NOT_FOUND",
            Map.of("id", id)
        );
    }
}
