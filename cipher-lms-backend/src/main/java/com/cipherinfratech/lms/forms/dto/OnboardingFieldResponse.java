package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnboardingFieldResponse {

    private Long fieldId;

    private String label;

    private String type;

    private boolean required;

    private String value;

}