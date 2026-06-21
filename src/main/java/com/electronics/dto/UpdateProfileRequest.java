package com.electronics.dto;

import java.time.LocalDate;

public record UpdateProfileRequest(String firstName, String lastName, LocalDate birthDate,
        String profilePicUrl, String about) {
}