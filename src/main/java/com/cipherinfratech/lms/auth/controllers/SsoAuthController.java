package com.cipherinfratech.lms.auth.controllers;

import com.cipherinfratech.lms.organizations.entities.OrganizationApproval;
import com.cipherinfratech.lms.organizations.enums.ApprovalStatus;
import com.cipherinfratech.lms.organizations.repositories.OrganizationApprovalRepo;
import com.cipherinfratech.lms.security.SsoJwtService;
import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.entity.Users;
import com.cipherinfratech.lms.users.services.CustomUserDetailsService;
import com.cipherinfratech.lms.utils.ResponseModels;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("sso")
@RequiredArgsConstructor
public class SsoAuthController {

    private final SsoJwtService ssoJwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final OrganizationApprovalRepo organizationApprovalRepo;

    @PostMapping("/login")
    public ResponseEntity<?> ssoLogin(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseModels.error("Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7);

        Claims claims = ssoJwtService.validateSsoToken(token);
        if (claims == null) {
            return ResponseModels.error("Invalid or expired SSO token");
        }

        String email = ssoJwtService.extractEmail(claims);
        if (email == null) {
            return ResponseModels.error("SSO token does not contain a valid subject");
        }

        Users user;
        try {
            user = (Users) customUserDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            return ResponseModels.error("User not registered in LMS: " + email);
        }

        if (!user.getStatus()) {

            if (user.getOrganizations() != null) {

                OrganizationApproval approval = organizationApprovalRepo
                        .findByOrganization_OrganizationId(user.getOrganizations().getOrganizationId())
                        .orElse(null);

                if (approval != null) {

                    if (approval.getApprovalStatus() == ApprovalStatus.PENDING) {
                        return ResponseModels.error("Your organization registration is pending admin approval. Please contact support.");
                    }

                    if (approval.getApprovalStatus() == ApprovalStatus.REJECTED) {
                        return ResponseModels.error("Your organization registration was rejected. Please contact support.");
                    }
                }
            }

            return ResponseModels.error("Account is disabled");
        }

//        String lmsToken = jwtService.generateToken(user);

        Map<String, Object> loginResponse = new HashMap<>();
        loginResponse.put("status", "success");
        loginResponse.put("token", token);      // return same SSO token
        loginResponse.put("userId", user.getUserId());
        loginResponse.put("email", user.getEmailId());
        loginResponse.put("name", user.getName());
        loginResponse.put("contact", user.getContactNo());
        loginResponse.put("role", user.getRole());
        loginResponse.put("profilePic", user.getProfilePic());

        if (user.getRole().equals(Roles.ORGANIZATION) || user.getRole().equals(Roles.ORG_ADMIN)) {
            loginResponse.put("organizationId", user.getOrganizations().getOrganizationId());
        }

        return ResponseEntity.ok(loginResponse);
    }
}
