package com.electronics.exception;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/*
    response = {
        "message": ...,
        "statusCode": ...,
        "errorCode": ...,
        "info": {
            "id": ...
        }
    }
*/

@Getter
public abstract class EcommerceException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;
    private final Map<String, Object> info;

    public EcommerceException(String message, int statusCode, String errorCode) {
        this(message, statusCode, errorCode, Map.of());
    }

    public EcommerceException(String message, int statusCode, String errorCode,
            Map<String, Object> info) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.info = info;
    }
}
