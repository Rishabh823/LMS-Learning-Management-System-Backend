package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnboardingAnswerResponse {

    private Long fieldId;

    private String section;

    private String fieldName;

    private String label;

    private String fieldType;

    private boolean required;

    private String value;

}