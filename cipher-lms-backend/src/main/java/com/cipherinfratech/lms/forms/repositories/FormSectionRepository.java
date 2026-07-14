package com.cipherinfratech.lms.forms.repositories;

import com.cipherinfratech.lms.forms.entities.FormSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormSectionRepository extends JpaRepository<FormSection, Long> {
}