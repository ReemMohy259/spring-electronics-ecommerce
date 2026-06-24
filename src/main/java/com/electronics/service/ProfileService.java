package com.electronics.service;

import com.electronics.dto.CurrentUser;
import com.electronics.dto.ProfileResponse;
import com.electronics.dto.UpdateProfileRequest;
import com.electronics.entity.Merchant;
import com.electronics.entity.Role;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.UserRepository;
import com.electronics.util.CurrentUserDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final CurrentUserDataUtil currentUserDataUtil;
    private final MerchantRepository merchantRepository;

    public ProfileResponse getCurrentProfile() {
        CurrentUser user = currentUserDataUtil.getCurrentUser();

        String about = null;
        if (user.roles().contains(Role.MERCHANT)) {
            Optional<Merchant> m = merchantRepository.findByEmail(user.email());
            if (m.isPresent())
                about = m.get().getAbout();
        }

        return new ProfileResponse(
            user.firstName(),
            user.lastName(),
            user.email(),
            user.username(),
            user.roles(),
            user.birthDate(),
            user.profilePicUrl(),
            about);
    }

    // TODO: Integrate with keycloak
    public void updateProfile(UpdateProfileRequest request) {

        // User user = getCurrentUser();
        //
        // if (request.firstName() != null) {
        // user.setFirstName(request.firstName());
        // }
        //
        // if (request.lastName() != null) {
        // user.setLastName(request.lastName());
        // }
        //
        // if (request.birthDate() != null) {
        // user.setBirthDate(request.birthDate());
        // }
        //
        // if (request.profilePicUrl() != null) {
        // user.setProfilePicUrl(request.profilePicUrl());
        // }
        //
        // if (user.getRole() == Role.MERCHANT && user instanceof Merchant merchant
        // && request.about() != null) {
        // merchant.setAbout(request.about());
        // }
        //
        // userRepository.save(user);
    }

}
