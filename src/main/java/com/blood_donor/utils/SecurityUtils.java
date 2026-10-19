package com.blood_donor.utils;

import com.blood_donor.exception.ApiException;
import com.blood_donor.repository.user_repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// ✅ S: Sirf security utility ka kaam
// ✅ D: Interface pe depend — UserRepository
//
// Explanation:
// SecurityContextHolder → current logged-in user
// Email → userId DB se fetch
// Har controller mein yeh logic nahi — ek jagah
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    // ✅ Bug Fix 6: SecurityContextHolder se user nikalo
    // Explanation:
    // Authentication object → UserDetails → email
    // Email → DB → userId
    public Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw ApiException.unauthorized("User not authenticated");
        }

        String email = extractEmailFromAuth(authentication);

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> ApiException.notFound(
                        "User not found: " + email
                ))
                .getId();
    }

    // ✅ Current user email nikalo
    public String getCurrentUserEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw ApiException.unauthorized("User not authenticated");
        }

        return extractEmailFromAuth(authentication);
    }

    // ✅ Role check
    // Explanation: Controller mein manual check avoid
    public boolean hasRole(String role) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return false;

        return authentication.getAuthorities()
                .stream()
                .anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_" + role)
                );
    }

    // ✅ Private helper
    private String extractEmailFromAuth(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String email) {
            return email;
        }

        throw ApiException.unauthorized("Cannot extract user from authentication");
    }
}