package com.cipherinfratech.lms.organizations.repositories;


import com.cipherinfratech.lms.organizations.dto.OrganizationListProjection;
import com.cipherinfratech.lms.organizations.entities.OrganizationProjection;
import com.cipherinfratech.lms.organizations.entities.OrganizationSummaryDTO;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.organizations.enums.ApprovalStatus;
import com.cipherinfratech.lms.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrganizationsRepo extends JpaRepository<Organizations, Long> {
    Organizations findByEmailId(String emailId);

    Organizations findByUsers(Users currUser);

    List<Organizations> findAllByStatusTrue();

    List<OrganizationSummaryDTO> findByStatusTrueOrderByFullNameAsc();

    @Query("SELECT o.fullName FROM Organizations o")
    List<String> findAllOrganizationNames();

    List<Organizations> findAllByStatusFalse();

    Page<Organizations> findAllByStatusTrue(Pageable pageable);
    Page<Organizations> findAllByStatusFalse(Pageable pageable);
    Page<Organizations> findAll(Pageable pageable); // already exists from JpaRepository

    //	// For org and trainer
	long countByOrganizationId(Long orgId);
	long countByOrganizationIdAndStatus(Long orgId, boolean status);

    // optimized projection fetch with groups
    @EntityGraph(attributePaths = {"groups"})
    @Query("select o from Organizations o")
    Page<OrganizationProjection> findAllProjected(Pageable pageable);

    @EntityGraph(attributePaths = {"groups"})
    @Query("select o from Organizations o where o.status = true")
    Page<OrganizationProjection> findAllActiveProjected(Pageable pageable);

    @EntityGraph(attributePaths = {"groups"})
    @Query("select o from Organizations o where o.status = false")
    Page<OrganizationProjection> findAllInactiveProjected(Pageable pageable);

//    @Query("""
//        SELECT
//            SUM(CASE WHEN o.status = true THEN 1 ELSE 0 END) AS activeCount,
//            SUM(CASE WHEN o.status = false THEN 1 ELSE 0 END) AS inactiveCount
//        FROM Organizations o
//    """)
//    OrganizationCountProjection getOrganizationCounts();

    boolean existsByOrganizationId(Long organizationId);

    List<OrganizationListProjection>
    findByStatusTrueOrderByOrganizationIdDesc();

    @Modifying
    @Transactional
    @Query("""
            UPDATE Organizations o
            SET o.status = :status
            WHERE o.organizationId = :organizationId
           """)
    void updateOrganizationStatus(
            @Param("organizationId") Long organizationId,
            @Param("status") boolean status
    );

    long countByStatusTrue();

    boolean existsByFullName(String fullName);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByPanNumber(String panNumber);

    @Query("""
            SELECT o FROM Organizations o
            WHERE (:search IS NULL OR :search = ''
                    OR LOWER(o.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(o.emailId) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:approvalStatus IS NULL OR o.approval.approvalStatus = :approvalStatus)
           """)
    Page<Organizations> searchOrganizations(
            @Param("search") String search,
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            Pageable pageable
    );
}
