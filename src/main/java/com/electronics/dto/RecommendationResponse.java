package com.electronics.dto;

public record RecommendationResponse(ProductResponse product, String reason, boolean aiGenerated) {
}
