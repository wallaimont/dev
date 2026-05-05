package com.orionerp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return principal;
    }

    public static Long currentUserId() {
        UserPrincipal principal = currentUser();
        return principal != null ? principal.getUserId() : null;
    }

    public static Long currentEmpresaId() {
        UserPrincipal principal = currentUser();
        return principal != null ? principal.getEmpresaId() : null;
    }

    public static Long currentFilialId() {
        UserPrincipal principal = currentUser();
        return principal != null ? principal.getFilialId() : null;
    }

    public static String currentUsername() {
        UserPrincipal principal = currentUser();
        return principal != null ? principal.getEmail() : "SYSTEM";
    }
}
