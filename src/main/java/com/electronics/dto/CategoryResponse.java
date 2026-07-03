package com.electronics.dto;

import com.electronics.entity.Category;

public record CategoryResponse(Integer id, String name, String slug, String lucideIconName) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getSlug(),
            category.getLucideIconName());
    }
}
