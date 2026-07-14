package com.cipherinfratech.lms.organizations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationListResponse {

    private long organizationId;
    private String organizationName;
    private String emailId;
    private String organizationType;
    private String industry;
    private String companySize;
    private String approvalStatus;
    private boolean status;
    private Date createdDate;
    private String adminName;
    private String adminEmail;

}
