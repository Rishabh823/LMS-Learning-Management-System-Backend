package com.cipherinfratech.lms.security;

import com.cipherinfratech.lms.users.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");

        log.info("JWT Filter triggered for URI: {}", requestURI);

        // 1. No token → skip
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No JWT token found in request");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Already authenticated → skip
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.info("User already authenticated, skipping JWT filter");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            log.info("JWT detected");

            // Extract claims
            String userEmail = jwtService.extractUserName(jwt);
            String role = jwtService.extractRole(jwt);
            String userId = jwtService.extractUserId(jwt);

            log.debug("Extracted JWT → email: {}, role: {}, userId: {}", userEmail, role, userId);

            // Validate token
            if (userEmail == null || role == null || userId == null) {
                log.error("JWT invalid: missing claims");
                filterChain.doFilter(request, response);
                return;
            }

            if (isTokenExpired(jwt)) {
                log.error("JWT expired for user: {}", userEmail);
                filterChain.doFilter(request, response);
                return;
            }

            log.info("JWT is valid for user: {}", userEmail);

            // Create authentication
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (!userDetails.isEnabled()) {
                log.warn("Account disabled for user: {}", userEmail);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Account is disabled");
                return;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,   // ✅ FIXED
                            null,
                            userDetails.getAuthorities()
                    );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // Set context
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            log.info("Security context set for user: {} with role: {}", userEmail, role);

        } catch (Exception ex) {
            log.error("JWT authentication failed: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/ws"); // 🔥 skip all websocket calls
    }

    private boolean isTokenExpired(String token) {
        try {
            return jwtService.extractUserName(token) == null
                    || jwtService.extractRole(token) == null
                    || jwtService.extractUserId(token) == null;
        } catch (Exception e) {
            return true;
        }
    }
}