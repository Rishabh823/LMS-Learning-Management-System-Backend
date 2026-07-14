package com.cipherinfratech.lms.forms.repositories;

import com.cipherinfratech.lms.forms.entities.FormSubmissionValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormSubmissionValueRepository extends JpaRepository<FormSubmissionValue, Long> {

    boolean existsByField_Id(Long fieldId);
}