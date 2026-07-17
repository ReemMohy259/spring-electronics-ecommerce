package com.electronics.dto;

import java.util.List;

public record AiRecommendationResult(List<Recommendation> recommendations) {

    public record Recommendation(Integer productId, String reason) {
    }
}
