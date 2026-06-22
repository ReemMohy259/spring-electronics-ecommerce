package com.electronics.dto;

public record ReviewResponse(Integer id, String username, Integer productId, Short rating,
    String comment) {
}