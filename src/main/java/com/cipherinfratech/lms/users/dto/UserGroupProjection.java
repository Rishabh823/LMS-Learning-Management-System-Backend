package com.cipherinfratech.lms.users.dto;

import java.util.UUID;

public interface UserGroupProjection {

    UUID getUserId();
    String getEmailId();
    String getName();
    String getGender();
    String getContactNo();
    String getProfilePic();
    Boolean getStatus();
    String getCreatedBy();
    String getInstructType();

    UsersProfileProjection getUsersProfile();

    interface UsersProfileProjection {
        Long getUserProfileId();
        String getDob();
        String getDesignation();
        String getDegreeName();
        String getPassingYear();
        String getPercentage();
        Integer getTotalExprience();
    }
}