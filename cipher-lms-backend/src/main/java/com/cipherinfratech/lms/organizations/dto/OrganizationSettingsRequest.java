package com.cipherinfratech.lms.organizations.dto;

import lombok.Data;

@Data
public class OrganizationSettingsRequest {

    private Integer maximumConcurrentSessions;

    private Boolean allowSelfRegistration;

    private Boolean allowTrainerRegistration;

    private Boolean allowExternalExaminee;

}
