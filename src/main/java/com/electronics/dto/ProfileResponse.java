package com.electronics.dto;

import com.electronics.entity.Role;

import java.time.LocalDate;
import java.util.Set;

public record ProfileResponse(Integer id, String firstName, String lastName, String email,
    String username, Set<Role> role, LocalDate birthDate, String profilePicUrl, String about,
    String businessName) {
}