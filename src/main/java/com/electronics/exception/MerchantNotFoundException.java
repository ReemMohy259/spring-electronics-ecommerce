package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class MerchantNotFoundException extends EcommerceException {

    public MerchantNotFoundException(Integer id) {
        super("Merchant with id=%s not found".formatted(id), HttpStatus.NOT_FOUND.value(),
            "MERCHANT_NOT_FOUND", Map.of("id", id));
    }
}
