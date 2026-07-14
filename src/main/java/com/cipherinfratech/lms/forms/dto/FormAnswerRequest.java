package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormAnswerRequest {

    private Long fieldId;

    private String value;

}