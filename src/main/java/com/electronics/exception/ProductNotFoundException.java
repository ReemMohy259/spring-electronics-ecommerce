package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ProductNotFoundException extends EcommerceException {

    private static final String ERROR_CODE = "PRODUCT_NOT_FOUND";
    private static final int STATUS_CODE = HttpStatus.NOT_FOUND.value();
    private static final String MESSAGE_TEMPLATE = "Product with id=%s not found";

    public ProductNotFoundException(Long id) {
        super(MESSAGE_TEMPLATE.formatted(id), STATUS_CODE, ERROR_CODE, Map.of("id", id));
    }

    public ProductNotFoundException(Long id, Map<String, Object> info) {
        super(MESSAGE_TEMPLATE.formatted(id), STATUS_CODE, ERROR_CODE, info);
    }
}
