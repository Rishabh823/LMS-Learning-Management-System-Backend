package com.cipherinfratech.lms.users.dto;

import com.cipherinfratech.lms.forms.dto.FormAnswerRequest;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private UUID userId;
    private String name;
    private String gender;
    private String contactNo;
    private List<FormAnswerRequest> onboardingAnswers;
}
