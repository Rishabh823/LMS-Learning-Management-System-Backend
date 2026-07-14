package com.cipherinfratech.lms.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrgAdminRequest {

    @NotBlank(message = "Name cant be blank")
    private String name;

    @NotBlank(message = "Email id cant be blank")
    @Email(message = "Invalid email format")
    private String emailId;

    @NotBlank(message = "Contact no cant be blank")
    private String contactNo;

    @NotBlank(message = "Gender cant be blank")
    private String gender;

    @NotBlank(message = "Password cant be blank")
    private String password;

    private String designation;

    private String department;
}
