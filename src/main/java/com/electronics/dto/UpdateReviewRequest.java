package com.electronics.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateReviewRequest {

    Integer productId;

    @Min(1)
    @Max(5)
    Short rating;

    @Size(max = 1000, message = "Comment too long")
    String comment;
}
