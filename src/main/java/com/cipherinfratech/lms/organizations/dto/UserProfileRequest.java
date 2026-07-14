package com.cipherinfratech.lms.organizations.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String degreeName;
    private String passingYear;
    private String percentage;
}
