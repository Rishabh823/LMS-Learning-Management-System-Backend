package com.cipherinfratech.lms.organizations.repositories;

import com.cipherinfratech.lms.organizations.entities.OrganizationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationSettingsRepo extends JpaRepository<OrganizationSettings, Long> {

    Optional<OrganizationSettings> findByOrganization_OrganizationId(long organizationId);
}
