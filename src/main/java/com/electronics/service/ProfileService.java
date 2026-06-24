package com.electronics.service;

import com.electronics.dto.CurrentUser;
import com.electronics.dto.ProfileResponse;
import com.electronics.dto.UpdateProfileRequest;
import com.electronics.entity.Merchant;
import com.electronics.entity.Role;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final MerchantRepository merchantRepository;

    // private User getCurrentUser() {
    // String email = SecurityUtil.getCurrentUserEmail();
    //
    // return userRepository.findByEmail(email)
    // .orElseThrow(() -> new UserNotFoundException(email));
    // }

    public ProfileResponse getCurrentProfile() {
        CurrentUser user = currentUserService.getCurrentUser();

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
