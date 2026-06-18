package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CategoryNotFoundException extends EcommerceException {

    public CategoryNotFoundException(Integer id) {
        super("Category with id=%s not found".formatted(id), HttpStatus.NOT_FOUND.value(),
                "CATEGORY_NOT_FOUND", Map.of("id", id));
    }
}
