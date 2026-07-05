package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class AiServiceUnavailableException extends EcommerceException {

    public AiServiceUnavailableException(String message, Map<String, Object> info) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE.value(), "AI_SERVICE_UNAVAILABLE", info);
    }
}
