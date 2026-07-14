package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FormSectionResponse {

    private Long id;

    private String title;

    private String description;

    private Integer displayOrder;

    private List<FormFieldResponse> fields;

}