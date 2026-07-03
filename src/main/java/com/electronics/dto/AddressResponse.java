package com.electronics.dto;

public record AddressResponse(Integer id, String addressName, String government, String city,
    String street, String buildingNo, String description) {
}