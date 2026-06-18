package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class DuplicateResourceException extends EcommerceException {

    public DuplicateResourceException(String resource, String field, Object value) {
        super("%s with %s=%s already exists".formatted(resource, field, value),
                HttpStatus.CONFLICT.value(), "DUPLICATE_RESOURCE",
                Map.of("resource", resource, "field", field, "value", value));
    }
}
