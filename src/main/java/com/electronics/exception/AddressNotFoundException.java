package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class AddressNotFoundException extends EcommerceException {

    private static final String ERROR_CODE = "ADDRESS_NOT_FOUND";
    private static final int STATUS_CODE = HttpStatus.NOT_FOUND.value();
    private static final String MESSAGE_TEMPLATE = "Address with id=%s not found";

    public AddressNotFoundException(Integer id) {
        super(MESSAGE_TEMPLATE.formatted(id), STATUS_CODE, ERROR_CODE, Map.of("id", id));
    }
}
