package com.cipherinfratech.lms.dashboard.dto;

import com.cipherinfratech.lms.users.entity.Roles;

import java.util.UUID;

public interface SidebarUserProjection {

    UUID getUserId();
    String getName();
    String getEmailId();
    String getProfilePic();
    Roles getRole();
}
