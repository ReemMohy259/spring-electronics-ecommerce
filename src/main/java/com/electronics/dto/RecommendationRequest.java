package com.electronics.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecommendationRequest(
    @NotBlank(message = "Preferences are required") @Size(max = 1000, message = "Preferences must not exceed 1000 characters") String preferences,

    @Min(value = 1, message = "Limit must be at least 1") @Max(value = 10, message = "Limit must not exceed 10") Integer limit) {
}
