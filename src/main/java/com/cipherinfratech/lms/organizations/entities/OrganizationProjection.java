package com.cipherinfratech.lms.organizations.entities;

public interface OrganizationProjection {
    Long getOrganizationId();
    String getLogoFileName();
    String getLogoFileType();
    String getFullName();
    String getEmailId();
    String getEmailIdAlternate();
    String getContact();
    String getContactAlternate();
    String getAboutOrganization();
    Boolean getStatus();

}

