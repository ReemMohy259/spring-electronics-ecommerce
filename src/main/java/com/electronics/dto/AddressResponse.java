package com.electronics.dto;

public record AddressResponse(Integer id, String government, String city, String street,
    String buildingNo, String description) {
}