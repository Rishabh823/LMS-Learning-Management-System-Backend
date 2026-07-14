package com.cipherinfratech.lms.organizations.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class OrganizationUpdateRequest {

    private String organizationName;

    private String legalBusinessName;

    private String organizationType;

    private String industry;

    private String companySize;

    private String website;

    private String description;

    @Email(message = "Invalid email format")
    private String emailId;

    private String emailIdAlternate;

    private String contact;

    private String contactAlternate;

    private String addressLine1;

    private String addressLine2;

    private String country;

    private String state;

    private String city;

    private String pincode;

    private String timezone;

    private String language;

    private String currency;

    private String defaultTheme;

    private String primaryColor;

    private String secondaryColor;
}
