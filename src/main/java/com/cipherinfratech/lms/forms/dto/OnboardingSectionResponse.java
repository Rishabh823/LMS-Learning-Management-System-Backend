package com.cipherinfratech.lms.forms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OnboardingSectionResponse {

    private String sectionTitle;

    private List<OnboardingFieldResponse> fields =
            new ArrayList<>();

}