package com.cipherinfratech.lms.forms.services;

import com.cipherinfratech.lms.forms.dto.SubmitFormRequest;
import com.cipherinfratech.lms.forms.entities.*;
import com.cipherinfratech.lms.forms.enums.FormStatus;
import com.cipherinfratech.lms.forms.repositories.*;
import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.entity.Users;
import com.cipherinfratech.lms.users.repositories.UsersRepo;
import com.cipherinfratech.lms.utils.ResponseModels;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl
        implements RegistrationService {

    private final FormRepository formRepository;

    private final FormSubmissionRepository submissionRepository;

    private final FormSubmissionValueRepository submissionValueRepository;

    private final FormFieldRepository fieldRepository;

    private final UsersRepo usersRepo;

    private final PasswordEncoder passwordEncoder;

    //--------------------------------------

    @Override
    public ResponseEntity<Object> registerUser(
            Long formId,
            SubmitFormRequest request) {

        // implementation in next response
        Form form = formRepository
                .findById(formId)
                .orElse(null);

        if (form == null) {
            return ResponseModels.error("Form not found.");
        }

        if (form.getStatus() != FormStatus.PUBLISHED) {
            return ResponseModels.error("Form is not published.");
        }

        /*
         * Prevent duplicate submission
         */

        if (usersRepo.existsByEmailId(request.getEmail())) {

            return ResponseModels.error(
                    "Email already registered."
            );
        }

        if (usersRepo.existsByContactNo(request.getPhone())) {

            return ResponseModels.error(
                    "Phone already registered."
            );
        }

        Users user = new Users();

        user.setName(request.getName());

        user.setEmailId(request.getEmail());

        user.setContactNo(request.getPhone());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setOrganizations(form.getOrganization());

        user.setRole(Roles.STUDENT);

        user.setStatus(true);

        /*
         * Your Users entity currently has:
         * @NotNull private String gender;
         *
         * Set a temporary value or redesign this field.
         */
        user.setGender(request.getGender());

        user = usersRepo.save(user);

        /*
         * Create Submission
         */

        FormSubmission submission = new FormSubmission();

        submission.setForm(form);

        submission.setOrganization(form.getOrganization());

        submission.setUser(user);

        submission.setSubmitted(true);

        submission = submissionRepository.save(submission);

        /*
         * Save Answers
         */

        for (var answer : request.getAnswers()) {

            FormField field = fieldRepository
                    .findById(answer.getFieldId())
                    .orElse(null);

            if (field == null) {
                continue;
            }

            FormSubmissionValue value =
                    new FormSubmissionValue();

            value.setSubmission(submission);

            value.setField(field);

            value.setValue(answer.getValue());

            submissionValueRepository.save(value);
        }

        return ResponseModels.success(
                "Registration completed successfully."
        );
    }

}