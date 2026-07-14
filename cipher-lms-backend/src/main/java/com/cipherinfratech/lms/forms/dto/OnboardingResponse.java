package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OnboardingResponse {

    private boolean submitted;

    private Date submittedDate;

    private List<OnboardingAnswerResponse> answers =
            new ArrayList<>();

}