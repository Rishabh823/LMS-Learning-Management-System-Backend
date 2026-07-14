package com.cipherinfratech.lms.forms.services;

import com.cipherinfratech.lms.forms.dto.*;
import com.cipherinfratech.lms.forms.entities.*;
import com.cipherinfratech.lms.forms.enums.FieldType;
import com.cipherinfratech.lms.forms.enums.FormStatus;
import com.cipherinfratech.lms.forms.repositories.FormFieldRepository;
import com.cipherinfratech.lms.forms.repositories.FormRepository;
import com.cipherinfratech.lms.forms.repositories.FormSubmissionValueRepository;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.users.entity.Users;
import com.cipherinfratech.lms.utils.FileFormats;
import com.cipherinfratech.lms.utils.FileUtils;
import com.cipherinfratech.lms.utils.ResponseModels;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;
    private final FormFieldRepository formFieldRepository;
    private final FormSubmissionValueRepository formSubmissionValueRepository;

    /**
     * Returns currently logged-in user
     */
    private Users getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (Users) authentication.getPrincipal();
    }

    /**
     * Returns current organization
     */
    private Organizations getCurrentOrganization() {

        Users user = getCurrentUser();

        if (user == null) {
            return null;
        }

        return user.getOrganizations();
    }

    @Override
    public ResponseEntity<Object> createForm(CreateFormRequest request) {

        Organizations organization = getCurrentOrganization();

        if (organization == null) {
            return ResponseModels.error("Organization not found.");
        }

        if (formRepository.existsByOrganization(organization)) {
            return ResponseModels.error(
                    "Onboarding form already exists for this organization."
            );
        }

        Form form = new Form();

        form.setOrganization(organization);
        form.setTitle(request.getTitle());
        form.setDescription(request.getDescription());
        form.setStatus(FormStatus.DRAFT);

        List<FormSection> sectionList = new ArrayList<>();

        if (request.getSections() != null) {

            for (FormSectionRequest sectionRequest : request.getSections()) {

                FormSection section = new FormSection();

                section.setForm(form);
                section.setTitle(sectionRequest.getTitle());
                section.setDescription(sectionRequest.getDescription());
                section.setDisplayOrder(sectionRequest.getDisplayOrder());

                List<FormField> fieldList = new ArrayList<>();

                if (sectionRequest.getFields() != null) {

                    for (FormFieldRequest fieldRequest :
                            sectionRequest.getFields()) {

                        FormField field = new FormField();

                        field.setSection(section);

                        field.setLabel(fieldRequest.getLabel());
                        field.setFieldName(fieldRequest.getFieldName());
                        field.setFieldType(fieldRequest.getFieldType());

                        field.setPlaceholder(
                                fieldRequest.getPlaceholder());

                        field.setDefaultValue(
                                fieldRequest.getDefaultValue());

                        field.setRequired(
                                fieldRequest.isRequired());

                        field.setReadOnly(
                                fieldRequest.isReadOnly());

                        field.setHidden(
                                fieldRequest.isHidden());

                        field.setDisplayOrder(
                                fieldRequest.getDisplayOrder());

                        field.setMinLength(
                                fieldRequest.getMinLength());

                        field.setMaxLength(
                                fieldRequest.getMaxLength());

                        field.setMinValue(
                                fieldRequest.getMinValue());

                        field.setMaxValue(
                                fieldRequest.getMaxValue());

                        field.setRegexPattern(
                                fieldRequest.getRegexPattern());

                        List<FormFieldOption> optionList =
                                new ArrayList<>();
                        if (fieldRequest.getOptions() != null) {

                            for (FormFieldOptionRequest optionRequest :
                                    fieldRequest.getOptions()) {

                                FormFieldOption option = new FormFieldOption();

                                option.setField(field);

                                option.setLabel(optionRequest.getLabel());

                                option.setValue(optionRequest.getValue());

                                option.setDisplayOrder(
                                        optionRequest.getDisplayOrder()
                                );

                                optionList.add(option);
                            }
                        }

                        field.setOptions(optionList);

                        fieldList.add(field);

                    } // Field Loop End

                }

                section.setFields(fieldList);

                sectionList.add(section);

            } // Section Loop End

        }

        form.setSections(sectionList);

        /*
         * Save complete object graph
         *
         * Because of CascadeType.ALL
         *
         * Form
         *    ↓
         * Sections
         *    ↓
         * Fields
         *    ↓
         * Options
         *
         * Everything is automatically saved.
         */

        Form savedForm = formRepository.save(form);

        return ResponseModels.createWithPayload(
                "Onboarding form created successfully.",
                savedForm
        );

    }

    @Override
    @Transactional
    public ResponseEntity<Object> updateForm(UpdateFormRequest request) {

        Organizations organization = getCurrentOrganization();

        if (organization == null) {
            return ResponseModels.error("Organization not found.");
        }

        Form form = formRepository
                .findByOrganization(organization)
                .orElse(null);

        if (form == null) {
            return ResponseModels.error("Form not found.");
        }

        /*
         * Update Form Details
         */
        form.setTitle(request.getTitle());
        form.setDescription(request.getDescription());

        /*
         * Match existing sections/fields by title/fieldName so fields that
         * already have submitted answers keep their id instead of being
         * deleted and recreated (deleting them breaks the FK from
         * form_submission_values).
         */
        List<FormSection> existingSections = form.getSections();

        Map<String, FormSection> existingSectionsByTitle = existingSections.stream()
                .collect(Collectors.toMap(FormSection::getTitle, s -> s, (a, b) -> a));

        List<FormSection> updatedSections = new ArrayList<>();

        if (request.getSections() != null) {

            for (FormSectionRequest sectionRequest : request.getSections()) {

                FormSection section = existingSectionsByTitle.remove(sectionRequest.getTitle());

                if (section == null) {
                    section = new FormSection();
                    section.setForm(form);
                }

                section.setTitle(sectionRequest.getTitle());
                section.setDescription(sectionRequest.getDescription());
                section.setDisplayOrder(sectionRequest.getDisplayOrder());

                List<FormField> existingFields = section.getFields();

                Map<String, FormField> existingFieldsByName = existingFields.stream()
                        .collect(Collectors.toMap(FormField::getFieldName, f -> f, (a, b) -> a));

                List<FormField> updatedFields = new ArrayList<>();

                if (sectionRequest.getFields() != null) {

                    for (FormFieldRequest fieldRequest : sectionRequest.getFields()) {

                        FormField field = existingFieldsByName.remove(fieldRequest.getFieldName());

                        if (field == null) {
                            field = new FormField();
                            field.setSection(section);
                        }

                        field.setLabel(fieldRequest.getLabel());
                        field.setFieldName(fieldRequest.getFieldName());
                        field.setFieldType(fieldRequest.getFieldType());
                        field.setPlaceholder(fieldRequest.getPlaceholder());
                        field.setDefaultValue(fieldRequest.getDefaultValue());
                        field.setRequired(fieldRequest.isRequired());
                        field.setReadOnly(fieldRequest.isReadOnly());
                        field.setHidden(fieldRequest.isHidden());
                        field.setDisplayOrder(fieldRequest.getDisplayOrder());
                        field.setMinLength(fieldRequest.getMinLength());
                        field.setMaxLength(fieldRequest.getMaxLength());
                        field.setMinValue(fieldRequest.getMinValue());
                        field.setMaxValue(fieldRequest.getMaxValue());
                        field.setRegexPattern(fieldRequest.getRegexPattern());

                        field.getOptions().clear();

                        if (fieldRequest.getOptions() != null) {

                            for (FormFieldOptionRequest optionRequest : fieldRequest.getOptions()) {

                                FormFieldOption option = new FormFieldOption();

                                option.setField(field);
                                option.setLabel(optionRequest.getLabel());
                                option.setValue(optionRequest.getValue());
                                option.setDisplayOrder(optionRequest.getDisplayOrder());

                                // DON'T use setOptions()
                                field.getOptions().add(option);
                            }
                        }

                        updatedFields.add(field);
                    }
                }

                /*
                 * Fields dropped from the request: keep the ones that already
                 * have submitted answers (can't be deleted), drop the rest.
                 */
                for (FormField removedField : existingFieldsByName.values()) {
                    if (removedField.getId() != null &&
                            formSubmissionValueRepository.existsByField_Id(removedField.getId())) {
                        updatedFields.add(removedField);
                    }
                }

                existingFields.clear();
                existingFields.addAll(updatedFields);

                // DON'T use setFields()
                updatedSections.add(section);
            }
        }

        /*
         * Sections dropped from the request: keep any whose fields still
         * have submitted answers.
         */
        for (FormSection removedSection : existingSectionsByTitle.values()) {

            boolean hasSubmissions = removedSection.getFields().stream()
                    .anyMatch(f -> f.getId() != null &&
                            formSubmissionValueRepository.existsByField_Id(f.getId()));

            if (hasSubmissions) {
                updatedSections.add(removedSection);
            }
        }

        // DON'T use setSections()
        existingSections.clear();
        existingSections.addAll(updatedSections);

        Form updatedForm = formRepository.save(form);

        return ResponseModels.successWithPayload(
                "Form updated successfully.",
                convertToResponse(updatedForm)
        );
    }

    @Override
    public ResponseEntity<Object> getMyForm() {

        Organizations organization = getCurrentOrganization();

        if (organization == null) {
            return ResponseModels.error("Organization not found.");
        }

        return formRepository.findByOrganization(organization)
                .<ResponseEntity<Object>>map(form ->
                        ResponseModels.successWithPayload(
                                "Form fetched successfully.",
                                convertToResponse(form)
                        )
                )
                .orElseGet(() ->
                        ResponseModels.error("Form not found.")
                );
    }

    @Override
    public ResponseEntity<Object> getFormByOrganization(Long organizationId) {
        return formRepository
                .findByOrganizationOrganizationIdAndStatus(
                        organizationId,
                        FormStatus.PUBLISHED
                )
                .<ResponseEntity<Object>>map(form ->
                        ResponseModels.successWithPayload(
                                "Form fetched successfully.",
                                form))
                .orElseGet(() ->
                        ResponseModels.error(
                                "Published form not found."
                        ));
    }

    @Override
    public ResponseEntity<Object> publishForm() {
        Organizations organization = getCurrentOrganization();

        if (organization == null) {
            return ResponseModels.error("Organization not found.");
        }

        Form form = formRepository
                .findByOrganization(organization)
                .orElse(null);

        if (form == null) {
            return ResponseModels.error("Form not found.");
        }

        form.setStatus(FormStatus.PUBLISHED);

        formRepository.save(form);

        return ResponseModels.update(
                "Form published successfully."
        );
    }

    @Override
    public ResponseEntity<Object> unpublishForm() {
        Organizations organization = getCurrentOrganization();

        if (organization == null) {
            return ResponseModels.error("Organization not found.");
        }

        Form form = formRepository
                .findByOrganization(organization)
                .orElse(null);

        if (form == null) {
            return ResponseModels.error("Form not found.");
        }

        form.setStatus(FormStatus.DRAFT);

        formRepository.save(form);

        return ResponseModels.update(
                "Form unpublished successfully."
        );
    }

    @Override
    public ResponseEntity<Object> deleteForm() {
        Organizations organization = getCurrentOrganization();

        if (organization == null) {
            return ResponseModels.error("Organization not found.");
        }

        Form form = formRepository
                .findByOrganization(organization)
                .orElse(null);

        if (form == null) {
            return ResponseModels.error("Form not found.");
        }

        formRepository.delete(form);

        return ResponseModels.deleted(
                "Form deleted successfully."
        );
    }

    private FormResponse convertToResponse(Form form) {

        FormResponse response = new FormResponse();

        response.setFormId(form.getFormId());

        response.setOrganizationId(
                form.getOrganization().getOrganizationId());

        response.setOrganizationName(
                form.getOrganization().getFullName());

        response.setTitle(form.getTitle());

        response.setDescription(form.getDescription());

        response.setStatus(form.getStatus());

        List<FormSectionResponse> sectionResponses = new ArrayList<>();

        if (form.getSections() != null) {

            for (FormSection section : form.getSections()) {

                FormSectionResponse sectionResponse =
                        new FormSectionResponse();

                sectionResponse.setId(section.getId());

                sectionResponse.setTitle(section.getTitle());

                sectionResponse.setDescription(
                        section.getDescription());

                sectionResponse.setDisplayOrder(
                        section.getDisplayOrder());

                List<FormFieldResponse> fieldResponses =
                        new ArrayList<>();

                if (section.getFields() != null) {

                    for (FormField field : section.getFields()) {

                        FormFieldResponse fieldResponse =
                                new FormFieldResponse();

                        fieldResponse.setId(field.getId());

                        fieldResponse.setLabel(field.getLabel());

                        fieldResponse.setFieldName(
                                field.getFieldName());

                        fieldResponse.setFieldType(
                                field.getFieldType());

                        fieldResponse.setPlaceholder(
                                field.getPlaceholder());

                        fieldResponse.setDefaultValue(
                                field.getDefaultValue());

                        fieldResponse.setRequired(
                                field.isRequired());

                        fieldResponse.setReadOnly(
                                field.isReadOnly());

                        fieldResponse.setHidden(
                                field.isHidden());

                        fieldResponse.setDisplayOrder(
                                field.getDisplayOrder());

                        fieldResponse.setMinLength(
                                field.getMinLength());

                        fieldResponse.setMaxLength(
                                field.getMaxLength());

                        fieldResponse.setMinValue(
                                field.getMinValue());

                        fieldResponse.setMaxValue(
                                field.getMaxValue());

                        fieldResponse.setRegexPattern(
                                field.getRegexPattern());

                        List<FormFieldOptionResponse> optionResponses =
                                new ArrayList<>();

                        if (field.getOptions() != null) {

                            for (FormFieldOption option :
                                    field.getOptions()) {

                                FormFieldOptionResponse optionResponse =
                                        new FormFieldOptionResponse();

                                optionResponse.setId(option.getId());

                                optionResponse.setLabel(
                                        option.getLabel());

                                optionResponse.setValue(
                                        option.getValue());

                                optionResponse.setDisplayOrder(
                                        option.getDisplayOrder());

                                optionResponses.add(optionResponse);
                            }
                        }

                        fieldResponse.setOptions(optionResponses);

                        fieldResponses.add(fieldResponse);
                    }
                }

                sectionResponse.setFields(fieldResponses);

                sectionResponses.add(sectionResponse);
            }
        }

        response.setSections(sectionResponses);

        return response;
    }

    @Override
    public ResponseEntity<Object> uploadFieldFile(Long fieldId, MultipartFile file) {

        FormField field = formFieldRepository.findById(fieldId).orElse(null);

        if (field == null) {
            return ResponseModels.error("Field not found.");
        }

        if (field.getFieldType() != FieldType.FILE) {
            return ResponseModels.error("This field does not accept file uploads.");
        }

        if (file == null || file.isEmpty()) {
            return ResponseModels.error("File cannot be empty");
        }

        long maxSizeInBytes = 10L * 1024 * 1024;
        if (file.getSize() > maxSizeInBytes) {
            return ResponseModels.error("File size should not exceed 10 MB");
        }

        String contentType = FileUtils.resolveContentType(
                file.getOriginalFilename(),
                file.getContentType()
        );

        if (contentType == null || !FileFormats.documentUploadFormat().contains(contentType)) {
            return ResponseModels.error(
                    "Invalid file type. Allowed types: " + FileFormats.documentUploadFormat()
            );
        }

        try {
            field.setFileName(file.getOriginalFilename());
            field.setFileType(contentType);
            field.setFileData(FileUtils.compressFile(file.getBytes()));
        } catch (IOException e) {
            return ResponseModels.exceptionError(e);
        }

        formFieldRepository.save(field);

        return ResponseModels.success("File uploaded successfully.");
    }

    @Override
    public ResponseEntity<Object> getFieldFile(Long fieldId) {

        FormField field = formFieldRepository.findById(fieldId).orElse(null);

        if (field == null) {
            return ResponseModels.error("Field not found.");
        }

        if (field.getFileData() == null) {
            return ResponseModels.successWithPayload("No file found for this field", (Object) null);
        }

        return ResponseModels.sendMedia(field.getFileData(), field.getFileType());
    }
}