package com.electronics.service;

import com.electronics.dto.ProfileResponse;
import com.electronics.dto.UpdateProfileRequest;
import com.electronics.entity.Merchant;
import com.electronics.entity.Role;
import com.electronics.entity.User;
import com.electronics.exception.UserNotFoundException;
import com.electronics.repository.UserRepository;
import com.electronics.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public ProfileResponse getCurrentProfile() {
        User user = getCurrentUser();

        String about = null;
        if (user.getRole() == Role.MERCHANT && user instanceof Merchant merchant) {
            about = merchant.getAbout();
        }

        return new ProfileResponse(user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getUsername(), user.getRole().name(), user.getBirthDate(),
                user.getProfilePicUrl(), about);
    }

    public void updateProfile(UpdateProfileRequest request) {

        User user = getCurrentUser();

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }

        if (request.profilePicUrl() != null) {
            user.setProfilePicUrl(request.profilePicUrl());
        }

        if (user.getRole() == Role.MERCHANT && user instanceof Merchant merchant
                && request.about() != null) {
            merchant.setAbout(request.about());
        }

        userRepository.save(user);
    }
}
