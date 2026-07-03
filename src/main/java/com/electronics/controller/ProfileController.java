package com.electronics.controller;

import com.electronics.dto.ProfileResponse;
import com.electronics.dto.UpdateProfileRequest;
import com.electronics.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MERCHANT')")
    public ProfileResponse getProfile() {
        return profileService.getCurrentProfile();
    }

    @PatchMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public void updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        profileService.updateProfile(request);
    }
}