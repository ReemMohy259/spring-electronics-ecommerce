package com.electronics.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductSearchResponse(
    List<ProductDto> products,
    long totalElements,
    int totalPages,
    int currentPage
) {
}
