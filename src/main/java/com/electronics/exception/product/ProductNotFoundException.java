package com.electronics.exception.product;

import com.electronics.exception.EcommerceException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ProductNotFoundException extends EcommerceException {

    private static final String ERROR_CODE = "PRODUCT_NOT_FOUND";
    private static final int STATUS_CODE = HttpStatus.NOT_FOUND.value();
    private static final String MESSAGE_TEMPLATE = "Product with id=%s not found";

    public ProductNotFoundException(Integer id) {
        super(MESSAGE_TEMPLATE.formatted(id), STATUS_CODE, ERROR_CODE, Map.of("id", id));
    }

    public ProductNotFoundException(List<Integer> ids) {
        super(MESSAGE_TEMPLATE.formatted(ids), STATUS_CODE, ERROR_CODE, Map.of("ids", ids));
    }

    public ProductNotFoundException(Integer id, Map<String, Object> info) {
        super(MESSAGE_TEMPLATE.formatted(id), STATUS_CODE, ERROR_CODE, info);
    }
}
