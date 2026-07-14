package com.cipherinfratech.lms.organizations.repositories;

import com.cipherinfratech.lms.organizations.entities.OrganizationApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationApprovalRepo extends JpaRepository<OrganizationApproval, Long> {

    Optional<OrganizationApproval> findByOrganization_OrganizationId(long organizationId);
}
