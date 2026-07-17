package com.electronics.exception.product;

import com.electronics.exception.EcommerceException;
import com.electronics.exception.ErrorCodes;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class InsufficientStockException extends EcommerceException {

    private static final String ERROR_CODE = ErrorCodes.INSUFFICIENT_STOCK;
    private static final int STATUS_CODE = HttpStatus.NOT_FOUND.value();
    private static final String MESSAGE_TEMPLATE = "Product with id=%s not found";

    public InsufficientStockException(
        Integer productId,
        int requestedQuantity,
        int availableQuantity) {
        super(String.format(
            "Insufficient stock for product with id=%d. Requested %d items but only %d are available.",
            productId,
            requestedQuantity,
            availableQuantity), HttpStatus.CONFLICT.value(), "INSUFFICIENT_STOCK",
            Map.of(
                "productId",
                productId,
                "requestedQuantity",
                requestedQuantity,
                "availableQuantity",
                availableQuantity));
    }
}
