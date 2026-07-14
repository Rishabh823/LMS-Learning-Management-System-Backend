package com.cipherinfratech.lms.forms.controllers;

import com.cipherinfratech.lms.forms.dto.CreateFormRequest;
import com.cipherinfratech.lms.forms.dto.UpdateFormRequest;
import com.cipherinfratech.lms.forms.services.FormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forms")
public class FormController {

    private final FormService formService;

    /**
     * Create Form
     */
    @PostMapping
    public ResponseEntity<Object> createForm(
            @Valid @RequestBody CreateFormRequest request) {

        return formService.createForm(request);
    }

    /**
     * Get Logged In Organization Form
     */
    @GetMapping("/me")
    public ResponseEntity<Object> getMyForm() {

        return formService.getMyForm();
    }

    /**
     * Update Form
     */
    @PutMapping
    public ResponseEntity<Object> updateForm(
            @Valid @RequestBody UpdateFormRequest request) {

        return formService.updateForm(request);
    }

    /**
     * Publish Form
     */
    @PutMapping("/publish")
    public ResponseEntity<Object> publishForm() {

        return formService.publishForm();
    }

    /**
     * Unpublish Form
     */
    @PutMapping("/unpublish")
    public ResponseEntity<Object> unpublishForm() {

        return formService.unpublishForm();
    }

    /**
     * Delete Form
     */
    @DeleteMapping
    public ResponseEntity<Object> deleteForm() {

        return formService.deleteForm();
    }

    /**
     * Get Public Form By Organization
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<Object> getFormByOrganization(
            @PathVariable Long organizationId) {

        return formService.getFormByOrganization(organizationId);
    }

    /**
     * Upload a file directly onto a FILE-type form field.
     * Independent of registration - callable in parallel with it.
     */
    @PostMapping(value = "/field/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFieldFile(
            @RequestParam Long fieldId,
            @RequestParam MultipartFile file) {

        return formService.uploadFieldFile(fieldId, file);
    }

    /**
     * Common file download endpoint for a FILE-type form field.
     */
    @GetMapping("/field/{fieldId}/file")
    public ResponseEntity<Object> getFieldFile(
            @PathVariable Long fieldId) {

        return formService.getFieldFile(fieldId);
    }

}