package com.cipherinfratech.lms.forms.repositories;

import com.cipherinfratech.lms.forms.entities.Form;
import com.cipherinfratech.lms.forms.enums.FormStatus;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {

    Optional<Form> findByOrganization(Organizations organization);

    boolean existsByOrganization(Organizations organization);


    Optional<Form> findByOrganizationAndStatus(
            Organizations organization,
            FormStatus status
    );

    Optional<Form> findByOrganizationOrganizationId(
            Long organizationId
    );

    Optional<Form> findByOrganizationOrganizationIdAndStatus(
            Long organizationId,
            FormStatus status
    );

    List<Form> findAllByStatus(FormStatus status);

}