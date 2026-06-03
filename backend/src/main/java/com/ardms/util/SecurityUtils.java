package com.ardms.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Utility class for Spring Security operations.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        }
        if (principal instanceof String) {
            return Optional.of((String) principal);
        }
        return Optional.empty();
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(roleWithPrefix));
    }
}
