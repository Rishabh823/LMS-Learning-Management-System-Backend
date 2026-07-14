package com.cipherinfratech.lms.users.dto;

import com.cipherinfratech.lms.forms.dto.OnboardingSectionResponse;
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

    private List<OnboardingSectionResponse> sections =
            new ArrayList<>();

}