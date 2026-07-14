package com.cipherinfratech.lms.organizations.repositories;

import com.cipherinfratech.lms.organizations.entities.OrganizationDocuments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationDocumentsRepo extends JpaRepository<OrganizationDocuments, Long> {

    Optional<OrganizationDocuments> findByOrganization_OrganizationId(long organizationId);
}
