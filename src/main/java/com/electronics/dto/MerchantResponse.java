package com.electronics.dto;

import java.time.LocalDate;

public record MerchantResponse(Integer id, String businessName, String logoUrl, Double rating,
    Long totalProducts, LocalDate memberSince) {
}
