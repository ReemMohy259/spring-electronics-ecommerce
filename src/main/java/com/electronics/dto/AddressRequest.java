package com.electronics.dto;

public record AddressRequest(String government, String city, String street, String buildingNo,
        String description) {
}