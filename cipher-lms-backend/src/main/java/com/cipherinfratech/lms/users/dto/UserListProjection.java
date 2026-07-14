package com.cipherinfratech.lms.users.dto;

import java.util.UUID;

public interface UserListProjection {
    UUID getUserId();

    String getEmailId();

    String getName();

    String getProfilePic();

    boolean getStatus();

    String getRole();

    String getGender();

}
