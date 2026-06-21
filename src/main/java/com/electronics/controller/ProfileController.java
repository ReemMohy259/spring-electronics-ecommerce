package com.electronics.controller;

import com.electronics.dto.ProfileResponse;
import com.electronics.dto.UpdateProfileRequest;
import com.electronics.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ProfileResponse getProfile() {
        return profileService.getCurrentProfile();
    }

    @PutMapping
    public void updateProfile(@RequestBody UpdateProfileRequest request) {
        profileService.updateProfile(request);
    }
}