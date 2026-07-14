package com.cipherinfratech.lms.users.entity;

import lombok.Data;

@Data
public class UserResponse {
    private String emailId;
    private String name;
    private String gender;
    private String contactNo;

}