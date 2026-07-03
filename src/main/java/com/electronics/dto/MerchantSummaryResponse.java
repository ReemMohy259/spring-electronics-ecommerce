package com.electronics.dto;

public record MerchantSummaryResponse(
    Integer id,
    String firstName,
    String lastName,
    String email,
    String businessName
) {

}
