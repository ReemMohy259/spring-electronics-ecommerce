package com.electronics.dto;

import java.util.List;

public record ReviewSummaryResponse(Double average, Long totalReviews,
    List<RatingBreakdownResponse> breakdown) {

    public record RatingBreakdownResponse(Integer stars, Long count) {
    }
}
