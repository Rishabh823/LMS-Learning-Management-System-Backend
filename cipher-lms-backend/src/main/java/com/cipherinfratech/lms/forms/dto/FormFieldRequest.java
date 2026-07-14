package com.cipherinfratech.lms.forms.dto;

import com.cipherinfratech.lms.forms.enums.FieldType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FormFieldRequest {

    private String label;

    private String fieldName;

    private FieldType fieldType;

    private String placeholder;

    private String defaultValue;

    private boolean required;

    private boolean readOnly;

    private boolean hidden;

    private Integer displayOrder;

    private Integer minLength;

    private Integer maxLength;

    private Integer minValue;

    private Integer maxValue;

    private String regexPattern;

    private List<FormFieldOptionRequest> options;

}