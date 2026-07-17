package com.electronics.dto;

import com.electronics.entity.Role;

import java.time.LocalDate;
import java.util.Set;

public record CurrentUser(Integer id, String keycloakId,

    String username, String email, String firstName, String lastName,

    Set<Role> roles,

    LocalDate birthDate, String profilePicUrl) {
}
