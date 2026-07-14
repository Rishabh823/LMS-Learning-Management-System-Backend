package com.cipherinfratech.lms.subscription.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminInfoDTO {

    @NotBlank(message = "Admin name should not be empty")
    private String adminName;

    @NotBlank(message = "Admin email should not be empty")
    @Email(message = "Invalid admin email id")
    private String adminEmail;

    @NotBlank(message = "Admin phone should not be empty")
    @Size(min = 8, max = 16, message = "Admin phone should be 8-16 digits")
    private String adminPhone;

    @NotBlank(message = "Gender should not be empty")
    private String gender;

    @NotBlank(message = "Password should not be empty")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;

    private String designation;

    private String department;

}
