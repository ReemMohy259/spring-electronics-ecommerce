package com.electronics.dto.category;

import com.electronics.entity.Category;

public record CategoryInfoResponse(
    Integer id,
    String name,
    Integer productsCount
) {

    public static CategoryInfoResponse from(Category category, Integer productsCount) {
        return new CategoryInfoResponse(
            category.getId(), category.getName(), productsCount);
    }
}
