package com.electronics.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record ProductRequest(
        @NotNull(message = "Merchant id is required") @Min(value = 1, message = "Merchant id must be positive") Integer merchantId,

        @NotBlank(message = "Product name is required") @Size(max = 255, message = "Product name must not exceed 255 characters") String name,

        String description,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.01", message = "Price must be greater than zero") @Digits(integer = 10, fraction = 2, message = "Price must contain at most 10 integer and 2 decimal digits") BigDecimal price,

        @NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock quantity cannot be negative") Integer stockQuantity,

        @Size(max = 100, message = "SKU must not exceed 100 characters") String sku,

        String imageUrl,

        String additionalInfo,

        @NotEmpty(message = "At least one category is required") Set<@NotNull(message = "Category id cannot be null") @Min(value = 1, message = "Category id must be positive") Integer> categoryIds) {
}
