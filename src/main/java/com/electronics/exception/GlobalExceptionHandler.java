package com.electronics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EcommerceException.class)
    public ResponseEntity<Map<String, Object>> handleEcommerceException(
            EcommerceException e) {
        return buildErrorResponse(e.getMessage(), e.getStatusCode(), e.getErrorCode(),
                e.getInfo());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleEcommerceException(Exception e) {
        return buildErrorResponse(e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR",
                Map.of());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message,
            int status, String errorCode, Map<String, Object> info) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("statusCode", status);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("info", info);
        errorResponse.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
