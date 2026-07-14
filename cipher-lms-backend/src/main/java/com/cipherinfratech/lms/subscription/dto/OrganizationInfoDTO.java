package com.cipherinfratech.lms.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizationInfoDTO {

    @NotBlank(message = "Organization name should not be empty")
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

    private String gstNumber;

    private String panNumber;

    private String website;

    private String description;

}
