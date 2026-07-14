package com.cipherinfratech.lms.forms.services;

import com.cipherinfratech.lms.forms.dto.CreateFormRequest;
import com.cipherinfratech.lms.forms.dto.UpdateFormRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FormService {

    ResponseEntity<Object> createForm(CreateFormRequest request);

    ResponseEntity<Object> updateForm(UpdateFormRequest request);

    ResponseEntity<Object> getMyForm();

    ResponseEntity<Object> getFormByOrganization(Long organizationId);

    ResponseEntity<Object> publishForm();

    ResponseEntity<Object> unpublishForm();

    ResponseEntity<Object> deleteForm();

    ResponseEntity<Object> uploadFieldFile(Long fieldId, MultipartFile file);

    ResponseEntity<Object> getFieldFile(Long fieldId);
}