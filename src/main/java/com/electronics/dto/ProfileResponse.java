package com.electronics.dto;

import java.time.LocalDate;

public record ProfileResponse(String firstName, String lastName, String email, String username,
    String role, LocalDate birthDate, String profilePicUrl, String about // only for merchants
) {
}