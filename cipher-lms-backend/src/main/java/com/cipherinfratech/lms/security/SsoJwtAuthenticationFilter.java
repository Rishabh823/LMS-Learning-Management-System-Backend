package com.cipherinfratech.lms.security;

import com.cipherinfratech.lms.users.services.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SsoJwtAuthenticationFilter extends OncePerRequestFilter {

    private final SsoJwtService ssoJwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // No token? Skip — let the existing filter chain handle it
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Already authenticated? Skip
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // Try to validate as SSO (RSA) token
        Claims claims = ssoJwtService.validateSsoToken(token);

        if (claims != null) {
            String email = ssoJwtService.extractEmail(claims);

            if (email != null) {
                try {
                    // Look up user in LMS's own database by email
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("SSO authentication successful for user: {}", email);

                } catch (Exception e) {
                    // User doesn't exist in LMS DB — don't authenticate
                    log.warn("SSO token valid but user {} not found in LMS database", email);
                }
            }
        }

        // If SSO didn't authenticate (not RSA token, or user not in LMS DB),
        // the existing JwtAuthenticationFilter will try next
        filterChain.doFilter(request, response);
    }
}