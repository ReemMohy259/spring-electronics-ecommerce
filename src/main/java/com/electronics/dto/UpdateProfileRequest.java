package com.electronics.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(

    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters") String firstName,

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters") String lastName,

    @Past(message = "Birth date must be in the past") LocalDate birthDate,

    @Size(max = 2048, message = "Profile picture URL is too long") String profilePicUrl,

    @Size(max = 5000, message = "About section cannot exceed 5000 characters") String about

) {
}