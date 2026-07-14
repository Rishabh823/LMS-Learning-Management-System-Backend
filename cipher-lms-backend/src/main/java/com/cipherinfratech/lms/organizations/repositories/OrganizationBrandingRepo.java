package com.cipherinfratech.lms.organizations.repositories;

import com.cipherinfratech.lms.organizations.entities.OrganizationBranding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationBrandingRepo extends JpaRepository<OrganizationBranding, Long> {

    Optional<OrganizationBranding> findByOrganization_OrganizationId(long organizationId);
}
