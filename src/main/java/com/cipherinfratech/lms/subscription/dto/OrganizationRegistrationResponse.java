package com.cipherinfratech.lms.subscription.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrganizationRegistrationResponse {

    private String token;
    private UUID userId;
    private String email;
    private String name;
    private String role;
    private long organizationId;
    private String organizationName;
    private String planCode;
    private String subscriptionStatus;

}
