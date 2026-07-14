package com.cipherinfratech.lms.organizations.dto;

import lombok.Data;

@Data
public class OrganizationSearchRequest {

    private String search;

    private String approvalStatus;

    private int page = 0;

    private int size = 10;

    private String sortBy = "organizationId";

    private String direction = "desc";

}
