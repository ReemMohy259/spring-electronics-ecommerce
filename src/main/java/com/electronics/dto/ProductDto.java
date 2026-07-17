package com.electronics.dto;

import com.electronics.document.ProductDocument;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record ProductDto(Integer id, String name, String description, BigDecimal price,
    String imageUrl, Double rating, List<String> categories, OffsetDateTime createdAt) {
    public static ProductDto from(ProductDocument doc) {
        return ProductDto.builder()
            .id(doc.getId())
            .name(doc.getName())
            .description(doc.getDescription())
            .price(doc.getPrice())
            .imageUrl(doc.getImageUrl())
            .rating(doc.getRating())
            .categories(doc.getCategories())
            .createdAt(doc.getCreatedAt())
            .build();
    }
}
