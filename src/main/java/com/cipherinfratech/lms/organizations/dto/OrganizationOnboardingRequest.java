package com.cipherinfratech.lms.organizations.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrganizationOnboardingRequest {

    // Step 1 - Organization Information

    @NotBlank(message = "Organization name should not be empty")
    @Size(min = 2, max = 100, message = "Organization name should be 2-100 characters")
    private String organizationName;

    @NotBlank(message = "Legal business name should not be empty")
    private String legalBusinessName;

    @NotBlank(message = "Organization type should not be empty")
    private String organizationType;

    @NotBlank(message = "Industry should not be empty")
    private String industry;

    @NotBlank(message = "Company size should not be empty")
    private String companySize;

    @NotBlank(message = "Registration number should not be empty")
    private String registrationNumber;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Invalid GST number")
    private String gstNumber;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number")
    private String panNumber;

    private String website;

    private String description;

    // Step 2 - Primary Administrator

    @NotBlank(message = "Admin name should not be empty")
    private String adminName;

    @NotBlank(message = "Admin email should not be empty")
    @Email(message = "Invalid admin email id")
    private String adminEmail;

    @NotBlank(message = "Admin phone should not be empty")
    @Size(min = 8, max = 16, message = "Admin phone should be 8-16 digits")
    private String adminPhone;

    @NotBlank(message = "Gender should not be empty")
    private String gender;

    @NotBlank(message = "Password should not be empty")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;

    private String designation;

    private String department;

    // Step 3 - Address

    private String addressLine1;

    private String addressLine2;

    private String country;

    private String state;

    private String city;

    private String pincode;

    // Step 5 - Workspace

    private String timezone;

    private String language;

    private String currency;

    private String defaultTheme;

    // Step 6 - Branding

    private String primaryColor;

    private String secondaryColor;

}
