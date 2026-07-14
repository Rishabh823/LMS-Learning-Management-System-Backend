package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateFormRequest {

    private String title;

    private String description;

    private List<FormSectionRequest> sections;

}