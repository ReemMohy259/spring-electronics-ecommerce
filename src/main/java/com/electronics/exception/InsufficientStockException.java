package com.electronics.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class InsufficientStockException extends EcommerceException {

    public InsufficientStockException(
        Integer productId,
        int requestedQuantity,
        int availableQuantity
    ) {
        super(
            String.format(
                "Insufficient stock. Requested %d items but only %d are available.",
                requestedQuantity,
                availableQuantity
            ),
            HttpStatus.CONFLICT.value(),
            "INSUFFICIENT_STOCK",
            Map.of(
                "productId", productId,
                "requestedQuantity", requestedQuantity,
                "availableQuantity", availableQuantity
            )
        );
    }
}
