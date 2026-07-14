package com.cipherinfratech.lms.forms.dto;

import com.cipherinfratech.lms.forms.enums.FormStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FormResponse {

    private Long formId;

    private Long organizationId;

    private String organizationName;

    private String title;

    private String description;

    private FormStatus status;

    private List<FormSectionResponse> sections;

}