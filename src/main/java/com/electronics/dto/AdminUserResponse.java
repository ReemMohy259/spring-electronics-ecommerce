package com.electronics.dto;

import java.time.LocalDate;

public record AdminUserResponse(String id, String email, String name, String role,
    LocalDate createdAt) {
}
