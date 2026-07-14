package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormFieldOptionRequest {

    private String label;

    private String value;

    private Integer displayOrder;

}