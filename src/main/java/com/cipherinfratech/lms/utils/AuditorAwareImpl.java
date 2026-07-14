package com.cipherinfratech.lms.utils;

import com.cipherinfratech.lms.users.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAware")
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("AuditorAware: No authentication found → SYSTEM");
            return Optional.of("SYSTEM");
        }

        if (!authentication.isAuthenticated()) {
            log.warn("AuditorAware: Not authenticated → SYSTEM");
            return Optional.of("SYSTEM");
        }

        Object principal = authentication.getPrincipal();

        log.info("AuditorAware: Principal class = {}", principal.getClass());

        // Case 1: Your entity directly
        if (principal instanceof Users user) {
            log.info("AuditorAware: Found Users → {}", user.getUsername());
            return Optional.of(user.getUsername());
        }

        // Case 2: Spring UserDetails (MOST IMPORTANT for you now)
        if (principal instanceof UserDetails userDetails) {
            log.info("AuditorAware: Found UserDetails → {}", userDetails.getUsername());
            return Optional.of(userDetails.getUsername());
        }

        // Case 3: fallback (String principal)
        if (principal instanceof String username) {
            log.info("AuditorAware: Found String → {}", username);
            return Optional.of(username);
        }

        log.warn("AuditorAware: Unknown principal type → SYSTEM");
        return Optional.of("SYSTEM");
    }
}