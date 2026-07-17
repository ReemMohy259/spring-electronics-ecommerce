package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class InvalidRequestException extends EcommerceException {

    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST");
    }

    public InvalidRequestException(String message, Map<String, Object> info) {
        super(message, HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST", info);
    }
}
