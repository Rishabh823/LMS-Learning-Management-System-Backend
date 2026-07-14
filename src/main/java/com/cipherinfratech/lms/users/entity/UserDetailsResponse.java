package com.cipherinfratech.lms.users.entity;

import java.util.UUID;

import com.cipherinfratech.lms.users.dto.OnboardingResponse;
import com.cipherinfratech.lms.users.dto.OrganizationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private UUID userId;
    private String emailId;
    private String name;
    private String gender;
    private Roles role;
    private String contactNo;
    private boolean status;
    private String createdBy;
    private String instructType;
    private String profilePic;
    private OnboardingResponse onboarding;
    private OrganizationResponse organization;

    public UserDetailsResponse(Users user) {
        this.userId = user.getUserId();
        this.emailId = user.getEmailId();
        this.name = user.getName();
        this.gender = user.getGender();
        this.role = user.getRole();
        this.contactNo = user.getContactNo();
        this.status = user.getStatus();
        this.createdBy = user.getCreatedBy();
        this.instructType = user.getInstructType();
        this.profilePic = user.getProfilePic();
    }
}