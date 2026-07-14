package com.cipherinfratech.lms.users.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID userId;
    private String emailId;
    private String name;
    private String gender;
    private String contactNo;
    private String profilePic;
    private Boolean status;
    private String createdBy;
    private String instructType;

//    private UsersProfileDto usersProfile;

//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class UsersProfileDto {
//        private Long userProfileId;
//        private String dob;
//        private String designation;
//        private String degreeName;
//        private String passingYear;
//        private String percentage;
//        private Integer totalExprience;
//    }
}