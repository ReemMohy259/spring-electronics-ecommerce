package com.electronics.dto;

import com.electronics.entity.Product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

public record ProductResponse(Integer id, Integer merchantId, String name, String description,
    BigDecimal price, Integer stockQuantity, Integer soldUnits, String sku, String imageUrl,
    String additionalInfo, OffsetDateTime createdAt, List<CategoryResponse> categories) {

    public static ProductResponse from(Product product) {
        List<CategoryResponse> categories = product.getCategories()
            .stream()
            .map(CategoryResponse::from)
            .sorted(Comparator.comparing(CategoryResponse::name, String.CASE_INSENSITIVE_ORDER))
            .toList();
        return new ProductResponse(
            product.getId(),
            product.getMerchant().getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getSoldUnits(),
            product.getSku(),
            product.getImageUrl(),
            product.getAdditionalInfo(),
            product.getCreatedAt(),
            categories);
    }
}
