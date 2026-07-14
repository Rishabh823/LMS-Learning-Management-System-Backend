package com.cipherinfratech.lms.forms.repositories;

import com.cipherinfratech.lms.forms.entities.Form;
import com.cipherinfratech.lms.forms.entities.FormField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormFieldRepository
        extends JpaRepository<FormField, Long> {

    List<FormField> findBySection_Form(Form form);

}