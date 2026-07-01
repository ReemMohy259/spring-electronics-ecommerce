package com.electronics.dto.category;

import com.electronics.entity.Category;

public record CategoryResponse(
    Integer id,
    String name
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
