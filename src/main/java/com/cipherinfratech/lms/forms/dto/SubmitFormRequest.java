package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmitFormRequest {

    // Mandatory Registration Fields

    private String name;

    private String email;

    private String phone;

    private String password;

    private String gender;

    // Dynamic Organization Fields

    private List<FormAnswerRequest> answers;

}