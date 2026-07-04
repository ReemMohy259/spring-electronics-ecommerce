package com.electronics.service;

import com.electronics.dto.CurrentUser;
import com.electronics.dto.ProfileResponse;
import com.electronics.dto.UpdateProfileRequest;
import com.electronics.entity.Merchant;
import com.electronics.entity.Role;
import com.electronics.entity.User;
import com.electronics.exception.InvalidRequestException;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.UserRepository;
import com.electronics.util.CurrentUserDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final CurrentUserDataUtil currentUserDataUtil;
    private final MerchantRepository merchantRepository;
    private final CurrentUserService currentUserService;
    private final KeycloakAdminService keycloakAdminService;
    private final CloudinaryService cloudinaryService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public ProfileResponse getCurrentProfile() {
        CurrentUser user = currentUserDataUtil.getCurrentUser();

        String about = null;
        String businessName = null;
        if (user.roles().contains(Role.MERCHANT)) {
            Optional<Merchant> m = merchantRepository.findByEmail(user.email());
            if (m.isPresent()) {
                about = m.get().getAbout();
                businessName = m.get().getBusinessName();
            }
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
            about,
            businessName);
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

        if (user instanceof Merchant merchant) {
            if (request.about() != null) {
                merchant.setAbout(request.about());
            }
            if (request.businessName() != null) {
                merchant.setBusinessName(request.businessName());
            }
            merchantRepository.save(merchant);
        } else {
            userRepository.save(user);
        }
    }

    @Transactional
    public String updateProfilePicture(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidRequestException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidRequestException("Only image files are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidRequestException("File size exceeds maximum allowed size of 5MB");
        }

        User user = currentUserService.getCurrentUserEntity();

        String oldProfilePic = user.getProfilePicUrl();
        if (oldProfilePic != null && !oldProfilePic.isBlank()) {
            try {
                String publicId = extractCloudinaryPublicId(oldProfilePic);
                if (publicId != null) {
                    cloudinaryService.deleteImage(publicId);
                }
            } catch (Exception ignored) {
            }
        }

        Map<?, ?> result = cloudinaryService.uploadImage(file);
        String imageUrl = (String) result.get("url");
        user.setProfilePicUrl(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    private String extractCloudinaryPublicId(String imageUrl) {
        try {
            String withoutExtension = imageUrl.substring(0, imageUrl.lastIndexOf('.'));
            return withoutExtension.substring(withoutExtension.lastIndexOf('/') + 1);
        } catch (Exception e) {
            return null;
        }
    }

}
