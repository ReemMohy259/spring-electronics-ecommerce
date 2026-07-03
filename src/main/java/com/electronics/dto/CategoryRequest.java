package com.electronics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

    @NotBlank(message = "Name is required") @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters") String name,

    @NotBlank(message = "Slug is required") @Size(min = 3, max = 100, message = "Slug must be between 3 and 100 characters") @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase alphanumeric with hyphens only (e.g. 'gaming-laptops')") String slug,

    @NotBlank(message = "Icon name is required") String lucideIconName) {
    private static final String DEFAULT_ICON = "tag";

    public CategoryRequest {
        if (lucideIconName == null || lucideIconName.isBlank()) {
            lucideIconName = DEFAULT_ICON;
        }
    }
}