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

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setBirthDate(request.birthDate());
        user.setProfilePicUrl(request.profilePicUrl());

        if (user.getRole() == Role.MERCHANT && user instanceof Merchant merchant) {
            merchant.setAbout(request.about());
        }

        userRepository.save(user);
    }
}
