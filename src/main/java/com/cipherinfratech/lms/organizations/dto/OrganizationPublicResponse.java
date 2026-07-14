package com.cipherinfratech.lms.organizations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationPublicResponse {

    private long organizationId;
    private String organizationName;
    private String description;
    private boolean hasLogo;
    private boolean status;

}
