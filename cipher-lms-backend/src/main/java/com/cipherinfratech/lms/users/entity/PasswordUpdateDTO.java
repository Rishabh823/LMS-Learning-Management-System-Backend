package com.cipherinfratech.lms.users.entity;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}