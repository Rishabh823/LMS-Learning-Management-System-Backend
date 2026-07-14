package com.cipherinfratech.lms.users.repositories;

import java.util.List;
import java.util.UUID;

import com.cipherinfratech.lms.dashboard.dto.SidebarUserProjection;
import com.cipherinfratech.lms.users.dto.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.entity.Users;

import jakarta.transaction.Transactional;

public interface UsersRepo extends JpaRepository<Users, UUID> {

	UserDetails findByEmailId(String emailId);

	@Query(value = "select * from users where email_id = ?1", nativeQuery = true)
	Users findByEmail(String emailId);


	@Transactional
	@Modifying
	@Query("update Users u set u.profilePic = :profilePic where u.userId = :userId")
	void updateProfilePic(@Param(value = "userId") UUID userId, @Param(value = "profilePic") String profilePic);

	Users findByUserId(UUID userId);

	List<Users> findAllByStatusTrue();

	int countByRoleAndOrganizationsOrganizationId(Roles role, Long organizationId);

    List<Users> findByRoleAndOrganizations_OrganizationId(Roles roles, Long orgId);

//    long countByRole(Roles role);

	boolean existsByContactNo(String contactNo);

	boolean existsByEmailId(String emailId);

	Page<UserProjection> findUserProjectedByRoleAndOrganizations_OrganizationId(
			Roles role,
			Long organizationId,
			Pageable pageable
	);

	@Query("""
        SELECT 
            u.userId AS userId,
            u.name AS name,
            u.emailId AS emailId,
            u.profilePic AS profilePic,
            u.role AS role
        FROM Users u
        WHERE u.emailId = :email
    """)
	SidebarUserProjection findSidebarUserByEmail(String email);

	@Query("SELECT u.emailId FROM Users u WHERE u.role = :role AND u.status = true")
	List<String> findEmailsByRole(@Param("role") Roles role);

	@Query("""
       SELECT u.emailId
       FROM Users u
       WHERE u.organizations.organizationId = :organizationId
       AND u.role = :role
       AND u.status = true
       """)

	void updateUserStatusByOrganizationId(
			@Param("organizationId") Long organizationId,
			@Param("status") boolean status
	);

	@Query("""
        select count(u) > 0
        from Users u
        where u.userId = :userId
        and u.organizations.organizationId = :organizationId
        and u.role in (
            com.cipherinfratech.lms.users.entity.Roles.TRAINER,
            com.cipherinfratech.lms.users.entity.Roles.STUDENT
        )
       """)
	boolean canOrganizationManageUser(
			@Param("userId") UUID userId,
			@Param("organizationId") Long organizationId
	);

}