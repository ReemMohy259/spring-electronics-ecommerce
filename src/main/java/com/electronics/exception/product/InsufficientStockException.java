package com.electronics.exception.product;

import com.electronics.exception.EcommerceException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class InsufficientStockException extends EcommerceException {

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
