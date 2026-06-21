package com.electronics.dto;

import jakarta.validation.constraints.*;

public record CreateReviewRequest(

        @NotNull(message = "Product id is required") Integer productId,

        @NotNull(message = "Rating is required") @Min(1) @Max(5) Short rating,

        @Size(max = 1000, message = "Comment too long") String comment) {
}
