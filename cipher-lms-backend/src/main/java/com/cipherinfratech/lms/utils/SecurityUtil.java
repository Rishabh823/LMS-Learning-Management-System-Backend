package com.cipherinfratech.lms.utils;

import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.entity.Users;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Users getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Users user) {
            return user;
        }

        throw new RuntimeException("Invalid authentication principal");
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmailId();
    }

    public static java.util.UUID getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static Roles getCurrentUserRole() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotFoundException("No authenticated user found");
        }

        GrantedAuthority authority = authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("No role found")
                );

        String role = authority.getAuthority();

        // ROLE_ADMIN -> ADMIN
        role = role.replace("ROLE_", "");

        return Roles.valueOf(role);
    }

    public static boolean hasRole(Roles role) {
        return getCurrentUserRole() == role;
    }

    public static boolean isAdmin() {
        return hasRole(Roles.ADMIN);
    }

    public static boolean isOrganization() {
        return hasRole(Roles.ORGANIZATION);
    }

    public static boolean isOrgAdmin() {
        return hasRole(Roles.ORG_ADMIN);
    }

    /**
     * True for the primary org owner (ORGANIZATION) as well as org-scoped
     * admins (ORG_ADMIN) created by that owner - both act on the same
     * organization's data.
     */
    public static boolean isOrganizationOrOrgAdmin() {
        return isOrganization() || isOrgAdmin();
    }

    public static boolean isTrainer() {
        return hasRole(Roles.TRAINER);
    }

    /**
     * Throws unless the current user is ADMIN or the ORGANIZATION user owning
     * the given organization. Reused across organization/subscription services
     * so a plain ORGANIZATION user can never act on another org's data.
     */
    public static void assertCanManageOrganization(long organizationId) {

        if (isAdmin()) {
            return;
        }

        Users currentUser = getCurrentUser();

        if (currentUser.getOrganizations() == null ||
                currentUser.getOrganizations().getOrganizationId() != organizationId) {

            throw new ValidationException("You are not allowed to manage this organization");
        }
    }
}