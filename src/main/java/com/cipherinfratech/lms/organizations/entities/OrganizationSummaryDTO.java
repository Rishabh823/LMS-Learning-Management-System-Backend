package com.cipherinfratech.lms.organizations.entities;

public class    OrganizationSummaryDTO {
    private String organizationId;
    private String fullName;

    public OrganizationSummaryDTO(long organizationId, String fullName) {
        this.organizationId = String.valueOf(organizationId);
        this.fullName = fullName;
    }

    // Getters and Setters
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
