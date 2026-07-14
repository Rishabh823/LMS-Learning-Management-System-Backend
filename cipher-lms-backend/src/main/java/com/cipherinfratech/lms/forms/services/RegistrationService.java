package com.cipherinfratech.lms.forms.services;

import com.cipherinfratech.lms.forms.dto.SubmitFormRequest;
import org.springframework.http.ResponseEntity;

public interface RegistrationService {

    ResponseEntity<Object> registerUser(
            Long formId,
            SubmitFormRequest request
    );

}