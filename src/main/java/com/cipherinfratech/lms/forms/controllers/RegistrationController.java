package com.cipherinfratech.lms.forms.controllers;

import com.cipherinfratech.lms.forms.dto.SubmitFormRequest;
import com.cipherinfratech.lms.forms.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/{formId}")
    public ResponseEntity<Object> registerUser(
            @PathVariable Long formId,
            @Valid @RequestBody SubmitFormRequest request
    ) {

        return registrationService.registerUser(
                formId,
                request
        );
    }
}