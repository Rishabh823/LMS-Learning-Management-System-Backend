package com.cipherinfratech.lms.forms.repositories;

import com.cipherinfratech.lms.forms.entities.FormFieldOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormFieldOptionRepository extends JpaRepository<FormFieldOption, Long> {
}