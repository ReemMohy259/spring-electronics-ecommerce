package com.electronics.controller;

import com.electronics.dto.ProfileResponse;
import com.electronics.dto.UpdateProfileRequest;
import com.electronics.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MERCHANT', 'ADMIN')")
    public void updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        profileService.updateProfile(request);
    }

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, String>> updateProfilePicture(
        @RequestParam("file") MultipartFile file) {
        String imageUrl = profileService.updateProfilePicture(file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}