package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class UserNotFoundException extends EcommerceException {

    public UserNotFoundException(String email) {
        super("User with email=%s not found".formatted(email), HttpStatus.NOT_FOUND.value(),
                "USER_NOT_FOUND", Map.of("email", email));
    }
}
