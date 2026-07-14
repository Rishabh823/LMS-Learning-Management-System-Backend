package com.cipherinfratech.lms.dashboard.dto;

public interface SidebarProjection {
    // Section (Module)
    Long getModuleId();
    String getModuleName();
    Integer getModulePosition();

    // Page
    Long getPageId();
    String getPageName();
    String getPageUrl();
    Integer getPagePosition();

}
