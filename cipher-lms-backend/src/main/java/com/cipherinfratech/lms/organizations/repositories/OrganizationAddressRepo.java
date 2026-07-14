package com.cipherinfratech.lms.organizations.repositories;

import com.cipherinfratech.lms.organizations.entities.OrganizationAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationAddressRepo extends JpaRepository<OrganizationAddress, Long> {

    Optional<OrganizationAddress> findByOrganization_OrganizationId(long organizationId);
}
