package com.electronics.service;

import com.electronics.dto.CurrentUser;
import com.electronics.dto.ProfileResponse;
import com.electronics.dto.UpdateProfileRequest;
import com.electronics.entity.Merchant;
import com.electronics.entity.Role;
import com.electronics.entity.User;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.UserRepository;
import com.electronics.util.CurrentUserDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final CurrentUserDataUtil currentUserDataUtil;
    private final MerchantRepository merchantRepository;
    private final CurrentUserService currentUserService;
    private final KeycloakAdminService keycloakAdminService;

    public ProfileResponse getCurrentProfile() {
        CurrentUser user = currentUserDataUtil.getCurrentUser();

        String about = null;
        if (user.roles().contains(Role.MERCHANT)) {
            Optional<Merchant> m = merchantRepository.findByEmail(user.email());
            if (m.isPresent())
                about = m.get().getAbout();
        }

        return new ProfileResponse(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.email(),
                user.username(),
                user.roles(),
                user.birthDate(),
                user.profilePicUrl(),
                about);
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        User user = currentUserService.getCurrentUserEntity();

        boolean keycloakUpdateNeeded = false;
        String newFirstName = null;
        String newLastName = null;

        if (request.firstName() != null) {
            newFirstName = request.firstName();
            keycloakUpdateNeeded = true;
        }

        if (request.lastName() != null) {
            newLastName = request.lastName();
            keycloakUpdateNeeded = true;
        }

        if (keycloakUpdateNeeded) {
            keycloakAdminService.updateUser(user.getKeycloakId(), newFirstName, newLastName);
        }

        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }

        if (request.profilePicUrl() != null) {
            user.setProfilePicUrl(request.profilePicUrl());
        }

        userRepository.save(user);
    }

}
