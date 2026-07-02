package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class FileNotFoundException extends EcommerceException {

    public FileNotFoundException(String filename) {
        super("file with name=%s not found".formatted(filename), HttpStatus.NOT_FOUND.value(),
            "FILE_NOT_FOUND", Map.of("name", filename));
    }
}
