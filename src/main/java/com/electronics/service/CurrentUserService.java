package com.electronics.service;

import com.electronics.dto.CurrentUser;
import com.electronics.entity.Role;
import com.electronics.entity.User;
import com.electronics.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    @Transactional
    public User ensureCurrentUserExists() {
        Jwt jwt = getCurrentJwt();
        String keycloakId = jwt.getSubject();

        User user = userRepository.findByKeycloakId(keycloakId)
            .orElseGet(() -> createUserFromJwt(jwt));

        if (Boolean.TRUE.equals(user.getDeleted())) {
            throw new AccessDeniedException("Account is deleted");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        Jwt jwt = getCurrentJwt();
        String keycloakId = jwt.getSubject();

        User user = userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new EntityNotFoundException("User not found in local database"));

        if (Boolean.TRUE.equals(user.getDeleted())) {
            throw new AccessDeniedException("Account is deleted");
        }

        return user;
    }

    @Transactional
    public CurrentUser getCurrentUser() {
        Jwt jwt = getCurrentJwt();
        User user = ensureCurrentUserExists();

        return toDto(user, jwt);
    }

    private User createUserFromJwt(Jwt jwt) {
        User user = new User();
        user.setKeycloakId(jwt.getSubject());
        user.setEmail(jwt.getClaimAsString("email"));
        user.setDeleted(false);

        return userRepository.save(user);
    }

    private CurrentUser toDto(User user, Jwt jwt) {
        return new CurrentUser(
            user.getId(),
            user.getKeycloakId(),
            jwt.getClaimAsString("preferred_username"),
            jwt.getClaimAsString("email"),
            jwt.getClaimAsString("given_name"),
            jwt.getClaimAsString("family_name"),
            extractRoles(jwt),
            user.getBirthDate(),
            user.getProfilePicUrl());
    }

    private Set<Role> extractRoles(Jwt jwt) {
        Object realmAccessObj = jwt.getClaims().get("realm_access");
        if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) {
            return Set.of();
        }

        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof Collection<?> roles)) {
            return Set.of();
        }

        return roles.stream().map(Object::toString).map(Role::valueOf).collect(Collectors.toSet());
    }

    private Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT found");
        }

        return jwt;
    }
}