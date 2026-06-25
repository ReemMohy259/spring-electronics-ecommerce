package com.electronics.util;

import com.electronics.dto.CurrentUser;
import com.electronics.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserDataUtil {

    private final CurrentUserService currentUserService;

    public CurrentUser getCurrentUser() {
        return currentUserService.getCurrentUser();
    }

    public String getCurrentUserEmail() {
        return currentUserService.getCurrentUser().email();
    }

    public Integer getCurrentUserId() {
        return currentUserService.getCurrentUser().id();
    }
}