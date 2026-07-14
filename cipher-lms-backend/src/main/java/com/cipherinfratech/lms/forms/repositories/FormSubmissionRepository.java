package com.cipherinfratech.lms.forms.repositories;

import com.cipherinfratech.lms.forms.entities.Form;
import com.cipherinfratech.lms.forms.entities.FormSubmission;
import com.cipherinfratech.lms.users.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FormSubmissionRepository
        extends JpaRepository<FormSubmission, Long> {

    Optional<FormSubmission> findByFormAndUser(
            Form form,
            Users user
    );

    boolean existsByFormAndUser(
            Form form,
            Users user
    );

    @EntityGraph(attributePaths = {
            "form",
            "organization",
            "values",
            "values.field",
            "values.field.section"
    })
    Optional<FormSubmission> findByUser_UserId(UUID userId);

}