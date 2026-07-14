package com.cipherinfratech.lms.subscription.services;

import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.notification.events.OrganizationCreatedEvent;
import com.cipherinfratech.lms.notification.publisher.NotificationEventPublisher;
import com.cipherinfratech.lms.organizations.entities.OrganizationApproval;
import com.cipherinfratech.lms.organizations.entities.OrganizationSettings;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.organizations.enums.ApprovalStatus;
import com.cipherinfratech.lms.organizations.repositories.OrganizationApprovalRepo;
import com.cipherinfratech.lms.organizations.repositories.OrganizationSettingsRepo;
import com.cipherinfratech.lms.organizations.repositories.OrganizationsRepo;
import com.cipherinfratech.lms.security.JwtService;
import com.cipherinfratech.lms.subscription.dto.AdminInfoDTO;
import com.cipherinfratech.lms.subscription.dto.OrganizationInfoDTO;
import com.cipherinfratech.lms.subscription.dto.OrganizationRegisterRequest;
import com.cipherinfratech.lms.subscription.dto.OrganizationRegistrationResponse;
import com.cipherinfratech.lms.subscription.entities.OrganizationSubscription;
import com.cipherinfratech.lms.subscription.entities.SubscriptionPlan;
import com.cipherinfratech.lms.subscription.enums.BillingCycle;
import com.cipherinfratech.lms.subscription.enums.PaymentStatus;
import com.cipherinfratech.lms.subscription.enums.SubscriptionStatus;
import com.cipherinfratech.lms.subscription.repositories.OrganizationSubscriptionRepo;
import com.cipherinfratech.lms.subscription.repositories.SubscriptionPlanRepo;
import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.entity.Users;
import com.cipherinfratech.lms.users.entity.UsersProfile;
import com.cipherinfratech.lms.users.repositories.UsersRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class OrganizationRegistrationServiceImpl implements OrganizationRegistrationService {

    private OrganizationsRepo organizationsRepo;
    private UsersRepo usersRepo;
    private OrganizationSettingsRepo organizationSettingsRepo;
    private OrganizationSubscriptionRepo organizationSubscriptionRepo;
    private OrganizationApprovalRepo organizationApprovalRepo;
    private SubscriptionPlanRepo subscriptionPlanRepo;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private NotificationEventPublisher publisher;

    @Override
    @Transactional
    public OrganizationRegistrationResponse registerFreePlan(OrganizationRegisterRequest request) {

        SubscriptionPlan plan = subscriptionPlanRepo.findById(request.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        if (plan.getPrice() != null && plan.getPrice().signum() > 0) {
            throw new ValidationException(
                    "This plan requires payment. Use /payments/create-order and /payments/verify instead."
            );
        }

        return createOrganizationWithPlan(request.getOrganization(), request.getAdmin(), plan);
    }

    @Override
    @Transactional
    public OrganizationRegistrationResponse createOrganizationWithPlan(
            OrganizationInfoDTO orgInfo,
            AdminInfoDTO adminInfo,
            SubscriptionPlan plan
    ) {

        if (usersRepo.existsByEmailId(adminInfo.getAdminEmail())) {
            throw new ValidationException("An admin already exists with this email");
        }

        if (organizationsRepo.existsByFullName(orgInfo.getOrganizationName())) {
            throw new ValidationException("An organization already exists with this name");
        }

        if (organizationsRepo.existsByRegistrationNumber(orgInfo.getRegistrationNumber())) {
            throw new ValidationException("An organization already exists with this registration number");
        }

        if (hasText(orgInfo.getGstNumber()) && organizationsRepo.existsByGstNumber(orgInfo.getGstNumber())) {
            throw new ValidationException("An organization already exists with this GST number");
        }

        if (hasText(orgInfo.getPanNumber()) && organizationsRepo.existsByPanNumber(orgInfo.getPanNumber())) {
            throw new ValidationException("An organization already exists with this PAN number");
        }

        Organizations organization = new Organizations();
        organization.setFullName(orgInfo.getOrganizationName());
        organization.setLegalBusinessName(orgInfo.getLegalBusinessName());
        organization.setOrganizationType(orgInfo.getOrganizationType());
        organization.setIndustry(orgInfo.getIndustry());
        organization.setCompanySize(orgInfo.getCompanySize());
        organization.setRegistrationNumber(orgInfo.getRegistrationNumber());
        organization.setGstNumber(orgInfo.getGstNumber());
        organization.setPanNumber(orgInfo.getPanNumber());
        organization.setWebsite(orgInfo.getWebsite());
        organization.setAboutOrganization(orgInfo.getDescription());
        organization.setEmailId(adminInfo.getAdminEmail());
        organization.setContact(adminInfo.getAdminPhone());
        // Instant ACTIVE - no admin approval gate for plan-based self-serve signup
        organization.setStatus(true);

        Organizations savedOrg = organizationsRepo.save(organization);

        Users admin = new Users();
        admin.setName(adminInfo.getAdminName());
        admin.setEmailId(adminInfo.getAdminEmail());
        admin.setContactNo(adminInfo.getAdminPhone());
        admin.setGender(adminInfo.getGender());
        admin.setPassword(passwordEncoder.encode(adminInfo.getPassword()));
        admin.setRole(Roles.ORGANIZATION);
        admin.setStatus(true);
        admin.setOrganizations(savedOrg);

        UsersProfile profile = new UsersProfile();
        profile.setDesignation(adminInfo.getDesignation());
        profile.setDepartment(adminInfo.getDepartment());
        admin.setUsersProfile(profile);

        Users savedAdmin = usersRepo.save(admin);

        OrganizationSettings settings = new OrganizationSettings();
        settings.setOrganization(savedOrg);
        organizationSettingsRepo.save(settings);

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(plan.getBillingCycle() == BillingCycle.YEARLY ? Calendar.YEAR : Calendar.MONTH, 1);

        OrganizationSubscription subscription = new OrganizationSubscription();
        subscription.setOrganization(savedOrg);
        subscription.setSubscriptionPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setSubscriptionStartDate(now);
        subscription.setSubscriptionEndDate(calendar.getTime());
        subscription.setAutoRenew(true);
        subscription.setPaymentStatus(PaymentStatus.SUCCESS);
        organizationSubscriptionRepo.save(subscription);

        OrganizationApproval approval = new OrganizationApproval();
        approval.setOrganization(savedOrg);
        approval.setApprovalStatus(ApprovalStatus.APPROVED);
        approval.setRemarks("Auto-approved via self-serve plan signup");
        approval.setApprovedBy("system");
        approval.setApprovedDate(now);
        organizationApprovalRepo.save(approval);

        List<String> adminEmails = usersRepo.findEmailsByRole(Roles.ADMIN);
        publisher.publish(new OrganizationCreatedEvent(
                savedOrg.getOrganizationId(),
                savedOrg.getFullName(),
                adminInfo.getAdminName(),
                savedOrg.getEmailId(),
                adminEmails
        ));

        OrganizationRegistrationResponse response = new OrganizationRegistrationResponse();
        response.setToken(jwtService.generateToken(savedAdmin));
        response.setUserId(savedAdmin.getUserId());
        response.setEmail(savedAdmin.getEmailId());
        response.setName(savedAdmin.getName());
        response.setRole(savedAdmin.getRole().name());
        response.setOrganizationId(savedOrg.getOrganizationId());
        response.setOrganizationName(savedOrg.getFullName());
        response.setPlanCode(plan.getPlanCode());
        response.setSubscriptionStatus(SubscriptionStatus.ACTIVE.name());

        return response;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
