package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ResourceInUseException extends EcommerceException {

    public ResourceInUseException(String resource, Integer id) {
        super("%s with id=%s is currently in use".formatted(resource, id),
            HttpStatus.CONFLICT.value(), "RESOURCE_IN_USE", Map.of("resource", resource, "id", id));
    }
}
