package com.cipherinfratech.lms.organizations.services;

import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.notification.events.OrganizationCreatedEvent;
import com.cipherinfratech.lms.notification.publisher.NotificationEventPublisher;
import com.cipherinfratech.lms.organizations.dto.*;
import com.cipherinfratech.lms.organizations.entities.*;
import com.cipherinfratech.lms.organizations.enums.ApprovalStatus;
import com.cipherinfratech.lms.organizations.repositories.*;
import com.cipherinfratech.lms.subscription.entities.OrganizationSubscription;
import com.cipherinfratech.lms.subscription.entities.SubscriptionPlan;
import com.cipherinfratech.lms.subscription.repositories.OrganizationSubscriptionRepo;
import com.cipherinfratech.lms.subscription.repositories.SubscriptionPlanRepo;
import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.entity.Users;
import com.cipherinfratech.lms.users.entity.UsersProfile;
import com.cipherinfratech.lms.users.repositories.UsersRepo;
import com.cipherinfratech.lms.users.services.UserService;
import com.cipherinfratech.lms.utils.FileFormats;
import com.cipherinfratech.lms.utils.FileUtils;
import com.cipherinfratech.lms.utils.MediaResponse;
import com.cipherinfratech.lms.utils.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrganizationsServiceImpl implements OrganizationsService{

    private OrganizationsRepo organizationsRepo;
    private UsersRepo usersRepo;
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private NotificationEventPublisher publisher;
    private OrganizationAddressRepo organizationAddressRepo;
    private OrganizationDocumentsRepo organizationDocumentsRepo;
    private OrganizationBrandingRepo organizationBrandingRepo;
    private OrganizationSettingsRepo organizationSettingsRepo;
    private OrganizationSubscriptionRepo organizationSubscriptionRepo;
    private OrganizationApprovalRepo organizationApprovalRepo;
    private SubscriptionPlanRepo subscriptionPlanRepo;

    @Override
    @Transactional
    public OrganizationDetailsResponse onboardOrganization(
            OrganizationOnboardingRequest request,
            MultipartFile logo,
            MultipartFile banner,
            MultipartFile favicon,
            MultipartFile certificateOfIncorporation,
            MultipartFile gstCertificate,
            MultipartFile panCard,
            MultipartFile msmeCertificate,
            MultipartFile isoCertificate
    ) {

        if (usersRepo.existsByEmailId(request.getAdminEmail())) {
            throw new ValidationException("An admin already exists with this email");
        }

        if (organizationsRepo.existsByFullName(request.getOrganizationName())) {
            throw new ValidationException("An organization already exists with this name");
        }

        if (organizationsRepo.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new ValidationException("An organization already exists with this registration number");
        }

        if (hasText(request.getGstNumber()) && organizationsRepo.existsByGstNumber(request.getGstNumber())) {
            throw new ValidationException("An organization already exists with this GST number");
        }

        if (hasText(request.getPanNumber()) && organizationsRepo.existsByPanNumber(request.getPanNumber())) {
            throw new ValidationException("An organization already exists with this PAN number");
        }

        // Step 1 - Organization Information
        Organizations organization = new Organizations();

        organization.setFullName(request.getOrganizationName());
        organization.setLegalBusinessName(request.getLegalBusinessName());
        organization.setOrganizationType(request.getOrganizationType());
        organization.setIndustry(request.getIndustry());
        organization.setCompanySize(request.getCompanySize());
        organization.setRegistrationNumber(request.getRegistrationNumber());
        organization.setGstNumber(request.getGstNumber());
        organization.setPanNumber(request.getPanNumber());
        organization.setWebsite(request.getWebsite());
        organization.setAboutOrganization(request.getDescription());

        organization.setEmailId(request.getAdminEmail());
        organization.setContact(request.getAdminPhone());

        // Newly onboarded organizations stay inactive until an ADMIN approves them
        organization.setStatus(false);

        Organizations savedOrg = organizationsRepo.save(organization);

        if (logo != null && !logo.isEmpty()) {
            FileUtils.validateFile(logo, FileFormats.userProfilePictureFormat(), 15);
            try {
                savedOrg.setLogoFileName(logo.getOriginalFilename());
                savedOrg.setLogoFileType(logo.getContentType());
                savedOrg.setLogo(FileUtils.compressFile(logo.getBytes()));
            } catch (IOException e) {
                throw new ValidationException("Unable to upload logo: " + e.getMessage());
            }
        }

        // Step 2 - Primary Administrator
        Users admin = new Users();
        admin.setName(request.getAdminName());
        admin.setEmailId(request.getAdminEmail());
        admin.setContactNo(request.getAdminPhone());
        admin.setGender(request.getGender());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Roles.ORGANIZATION);
        // Stays inactive until an ADMIN approves the organization (see approveOrganization)
        admin.setStatus(false);
        admin.setOrganizations(savedOrg);

        UsersProfile profile = new UsersProfile();
        profile.setDesignation(request.getDesignation());
        profile.setDepartment(request.getDepartment());
        profile.setAddress(request.getAddressLine1());
        admin.setUsersProfile(profile);

        userService.updateUser(admin);

        // Step 3 - Address
        OrganizationAddress address = new OrganizationAddress();
        address.setOrganization(savedOrg);
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCountry(request.getCountry());
        address.setState(request.getState());
        address.setCity(request.getCity());
        address.setPincode(request.getPincode());
        organizationAddressRepo.save(address);
        savedOrg.setAddress(address);

        // Step 4 - Documents
        OrganizationDocuments documents = new OrganizationDocuments();
        documents.setOrganization(savedOrg);
        attachDocument(documents::setCertificateOfIncorporationFile,
                documents::setCertificateOfIncorporationFileName,
                documents::setCertificateOfIncorporationFileType,
                certificateOfIncorporation);
        attachDocument(documents::setGstCertificateFile,
                documents::setGstCertificateFileName,
                documents::setGstCertificateFileType,
                gstCertificate);
        attachDocument(documents::setPanCardFile,
                documents::setPanCardFileName,
                documents::setPanCardFileType,
                panCard);
        attachDocument(documents::setMsmeCertificateFile,
                documents::setMsmeCertificateFileName,
                documents::setMsmeCertificateFileType,
                msmeCertificate);
        attachDocument(documents::setIsoCertificateFile,
                documents::setIsoCertificateFileName,
                documents::setIsoCertificateFileType,
                isoCertificate);
        organizationDocumentsRepo.save(documents);
        savedOrg.setDocuments(documents);

        // Step 5 - Workspace + Step 7 - Settings (defaults, real limits set later by ADMIN)
        OrganizationSettings settings = new OrganizationSettings();
        settings.setOrganization(savedOrg);
        settings.setTimezone(request.getTimezone());
        settings.setLanguage(request.getLanguage());
        settings.setCurrency(request.getCurrency());
        settings.setDefaultTheme(request.getDefaultTheme());
        organizationSettingsRepo.save(settings);
        savedOrg.setSettings(settings);

        // Step 6 - Branding
        OrganizationBranding branding = new OrganizationBranding();
        branding.setOrganization(savedOrg);
        branding.setPrimaryColor(request.getPrimaryColor());
        branding.setSecondaryColor(request.getSecondaryColor());

        if (banner != null && !banner.isEmpty()) {
            FileUtils.validateFile(banner, FileFormats.userProfilePictureFormat(), 15);
            try {
                branding.setBannerFileName(banner.getOriginalFilename());
                branding.setBannerFileType(banner.getContentType());
                branding.setBanner(FileUtils.compressFile(banner.getBytes()));
            } catch (IOException e) {
                throw new ValidationException("Unable to upload banner: " + e.getMessage());
            }
        }

        if (favicon != null && !favicon.isEmpty()) {
            FileUtils.validateFile(favicon, FileFormats.userProfilePictureFormat(), 5);
            try {
                branding.setFaviconFileName(favicon.getOriginalFilename());
                branding.setFaviconFileType(favicon.getContentType());
                branding.setFavicon(FileUtils.compressFile(favicon.getBytes()));
            } catch (IOException e) {
                throw new ValidationException("Unable to upload favicon: " + e.getMessage());
            }
        }

        organizationBrandingRepo.save(branding);
        savedOrg.setBranding(branding);

        // Subscription - defaults to the FREE plan (rich onboarding doesn't collect
        // a planId); an ADMIN can assign a different plan afterwards
        SubscriptionPlan defaultPlan = subscriptionPlanRepo.findByPlanCodeIgnoreCase("FREE")
                .orElseThrow(() -> new ValidationException("Default FREE plan not configured"));

        OrganizationSubscription subscription = new OrganizationSubscription();
        subscription.setOrganization(savedOrg);
        subscription.setSubscriptionPlan(defaultPlan);
        Date now = new Date();
        subscription.setSubscriptionStartDate(now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        subscription.setSubscriptionEndDate(calendar.getTime());
        organizationSubscriptionRepo.save(subscription);
        savedOrg.setSubscription(subscription);

        // Approval - PENDING until an ADMIN reviews it
        OrganizationApproval approval = new OrganizationApproval();
        approval.setOrganization(savedOrg);
        approval.setApprovalStatus(ApprovalStatus.PENDING);
        organizationApprovalRepo.save(approval);
        savedOrg.setApproval(approval);

        List<String> adminEmails = usersRepo.findEmailsByRole(Roles.ADMIN);

        publisher.publish(
                buildOrganizationCreatedEvent(savedOrg, request.getAdminName(), adminEmails)
        );

        return mapToDetailsResponse(savedOrg);
    }

    private void attachDocument(
            Consumer<byte[]> fileSetter,
            Consumer<String> nameSetter,
            Consumer<String> typeSetter,
            MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return;
        }

        FileUtils.validateFile(file, FileFormats.documentUploadFormat(), 10);

        try {
            nameSetter.accept(file.getOriginalFilename());
            typeSetter.accept(FileUtils.resolveContentType(file.getOriginalFilename(), file.getContentType()));
            fileSetter.accept(FileUtils.compressFile(file.getBytes()));
        } catch (IOException e) {
            throw new ValidationException("Unable to upload document: " + e.getMessage());
        }
    }

    @Override
    public List<Organizations> getAll() {
        return this.organizationsRepo.findAllByStatusTrue();
    }

    public Page<Organizations> getAllActive(Pageable pageable) {
        return this.organizationsRepo.findAllByStatusTrue(pageable);
    }

    @Override
    public void uploadLogo(UploadOrganizationLogoRequest request) throws IOException {

        Organizations organization = organizationsRepo.findById(request.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        MultipartFile logo = request.getLogo();

        FileUtils.validateFile(logo, List.of("image/jpeg", "image/jpg", "image/png"), 10);

        byte[] compressedLogo = FileUtils.compressFile(logo.getBytes());

        organization.setLogo(compressedLogo);
        organization.setLogoFileName(logo.getOriginalFilename());
        organization.setLogoFileType(logo.getContentType());

        organizationsRepo.save(organization);
    }


    public Page<OrganizationProjection> getAllActiveProjected(Pageable pageable) {
        return organizationsRepo.findAllActiveProjected(pageable);
    }

    @Override
    public MediaResponse getOrganizationLogo(Long organizationId) {

        Organizations organization = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        byte[] logo = organization.getLogo();

        if (logo == null || logo.length == 0) {
            throw new ValidationException("No logo found for this organization");
        }

        return new MediaResponse(
                logo,
                organization.getLogoFileType()
        );
    }

    @Transactional
    @Override
    public void toggleOrganizationStatus(Long organizationId, boolean activate) {

        if (!organizationsRepo.existsById(organizationId)) {
            throw new NotFoundException("Organization not found");
        }

        organizationsRepo.updateOrganizationStatus(organizationId, activate);

        usersRepo.updateUserStatusByOrganizationId(
                organizationId,
                activate
        );

//        courseRepo.updateCourseStatusByOrganizationId(
//                organizationId,
//                activate
//        );
    }

    public Page<OrganizationProjection> getAllInactiveProjected(Pageable pageable) {
        return organizationsRepo.findAllInactiveProjected(pageable);
    }

    public Page<OrganizationProjection> getAllProjected(Pageable pageable) {
        return organizationsRepo.findAllProjected(pageable);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private OrganizationCreatedEvent buildOrganizationCreatedEvent(
            Organizations org,
            String ownerName,
            List<String> adminEmails
    ) {
        return new OrganizationCreatedEvent(
                org.getOrganizationId(),
                org.getFullName(),
                ownerName,
                org.getEmailId(),
                adminEmails
        );
    }

    private OrganizationDetailsResponse mapToDetailsResponse(Organizations org) {

        OrganizationDetailsResponse response = new OrganizationDetailsResponse();

        response.setOrganizationId(org.getOrganizationId());
        response.setOrganizationName(org.getFullName());
        response.setLegalBusinessName(org.getLegalBusinessName());
        response.setOrganizationType(org.getOrganizationType());
        response.setIndustry(org.getIndustry());
        response.setCompanySize(org.getCompanySize());
        response.setRegistrationNumber(org.getRegistrationNumber());
        response.setGstNumber(org.getGstNumber());
        response.setPanNumber(org.getPanNumber());
        response.setWebsite(org.getWebsite());
        response.setDescription(org.getAboutOrganization());
        response.setHasLogo(org.getLogo() != null && org.getLogo().length > 0);
        response.setStatus(org.isStatus());
        response.setEmailId(org.getEmailId());
        response.setEmailIdAlternate(org.getEmailIdAlternate());
        response.setContact(org.getContact());
        response.setContactAlternate(org.getContactAlternate());
        response.setCreatedDate(org.getCreatedDate());

        List<Users> admins = usersRepo.findByRoleAndOrganizations_OrganizationId(
                Roles.ORGANIZATION, org.getOrganizationId());

        if (!admins.isEmpty()) {
            Users admin = admins.get(0);
            response.setAdminName(admin.getName());
            response.setAdminEmail(admin.getEmailId());
            response.setAdminPhone(admin.getContactNo());
            if (admin.getUsersProfile() != null) {
                response.setDesignation(admin.getUsersProfile().getDesignation());
                response.setDepartment(admin.getUsersProfile().getDepartment());
            }
        }

        OrganizationAddress address = org.getAddress();
        if (address != null) {
            response.setAddressLine1(address.getAddressLine1());
            response.setAddressLine2(address.getAddressLine2());
            response.setCountry(address.getCountry());
            response.setState(address.getState());
            response.setCity(address.getCity());
            response.setPincode(address.getPincode());
        }

        OrganizationDocuments documents = org.getDocuments();
        if (documents != null) {
            response.setHasCertificateOfIncorporation(documents.getCertificateOfIncorporationFile() != null);
            response.setHasGstCertificate(documents.getGstCertificateFile() != null);
            response.setHasPanCard(documents.getPanCardFile() != null);
            response.setHasMsmeCertificate(documents.getMsmeCertificateFile() != null);
            response.setHasIsoCertificate(documents.getIsoCertificateFile() != null);
        }

        OrganizationBranding branding = org.getBranding();
        if (branding != null) {
            response.setPrimaryColor(branding.getPrimaryColor());
            response.setSecondaryColor(branding.getSecondaryColor());
            response.setHasBanner(branding.getBanner() != null);
            response.setHasFavicon(branding.getFavicon() != null);
        }

        OrganizationSettings settings = org.getSettings();
        if (settings != null) {
            response.setTimezone(settings.getTimezone());
            response.setLanguage(settings.getLanguage());
            response.setCurrency(settings.getCurrency());
            response.setDefaultTheme(settings.getDefaultTheme());
            response.setEmailNotification(settings.isEmailNotification());
            response.setSmsNotification(settings.isSmsNotification());
            response.setWhatsappNotification(settings.isWhatsappNotification());
            response.setAllowSelfRegistration(settings.isAllowSelfRegistration());
            response.setAllowTrainerRegistration(settings.isAllowTrainerRegistration());
            response.setAllowExternalExaminee(settings.isAllowExternalExaminee());
            response.setMaximumConcurrentSessions(settings.getMaximumConcurrentSessions());
        }

        OrganizationSubscription subscription = org.getSubscription();
        if (subscription != null) {

            SubscriptionPlan plan = subscription.getSubscriptionPlan();

            if (plan != null) {
                response.setPlanId(plan.getPlanId());
                response.setPlanCode(plan.getPlanCode());
                response.setPlanName(plan.getPlanName());
                response.setMaxStudents(plan.getMaxStudents());
                response.setMaxTrainers(plan.getMaxTrainers());
                response.setMaxCourses(plan.getMaxCourses());
                response.setMaxGroups(plan.getMaxGroups());
                response.setMaxAdmins(plan.getMaxAdmins());
                response.setStorageGB(plan.getStorageGB());
                response.setAttendanceEnabled(plan.isAttendanceEnabled());
                response.setAssignmentEnabled(plan.isAssignmentEnabled());
                response.setCertificateEnabled(plan.isCertificateEnabled());
                response.setLiveClassEnabled(plan.isLiveClassEnabled());
                response.setDiscussionForumEnabled(plan.isDiscussionForumEnabled());
                response.setAiEnabled(plan.isAiEnabled());
                response.setBrandingEnabled(plan.isBrandingEnabled());
                response.setWhiteLabelEnabled(plan.isWhiteLabelEnabled());
                response.setCustomDomainEnabled(plan.isCustomDomainEnabled());
            }

            response.setSubscriptionStatus(subscription.getStatus() != null ? subscription.getStatus().name() : null);
            response.setSubscriptionStartDate(subscription.getSubscriptionStartDate());
            response.setSubscriptionEndDate(subscription.getSubscriptionEndDate());
            response.setAutoRenew(subscription.isAutoRenew());
            response.setPaymentStatus(subscription.getPaymentStatus() != null ? subscription.getPaymentStatus().name() : null);

            if (subscription.getPendingPlan() != null) {
                response.setPendingPlanCode(subscription.getPendingPlan().getPlanCode());
                response.setPendingPlanEffectiveDate(subscription.getPendingPlanEffectiveDate());
            }
        }

        OrganizationApproval approval = org.getApproval();
        if (approval != null) {
            response.setApprovalStatus(
                    approval.getApprovalStatus() != null ? approval.getApprovalStatus().name() : null);
            response.setRemarks(approval.getRemarks());
            response.setApprovedBy(approval.getApprovedBy());
            response.setApprovedDate(approval.getApprovedDate());
            response.setRejectedBy(approval.getRejectedBy());
            response.setRejectedDate(approval.getRejectedDate());
        }

        return response;
    }

    private OrganizationListResponse mapToListResponse(Organizations org) {

        List<Users> admins = usersRepo.findByRoleAndOrganizations_OrganizationId(
                Roles.ORGANIZATION, org.getOrganizationId());

        Users admin = admins.isEmpty() ? null : admins.get(0);

        return new OrganizationListResponse(
                org.getOrganizationId(),
                org.getFullName(),
                org.getEmailId(),
                org.getOrganizationType(),
                org.getIndustry(),
                org.getCompanySize(),
                org.getApproval() != null && org.getApproval().getApprovalStatus() != null
                        ? org.getApproval().getApprovalStatus().name() : null,
                org.isStatus(),
                org.getCreatedDate(),
                admin != null ? admin.getName() : null,
                admin != null ? admin.getEmailId() : null
        );
    }

    @Override
    @Transactional
    public OrganizationDetailsResponse updateOrganizationDetails(long organizationId, OrganizationUpdateRequest request) {

        SecurityUtil.assertCanManageOrganization(organizationId);

        Organizations existing = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        if (hasText(request.getEmailId()) && !request.getEmailId().equals(existing.getEmailId())) {

            Users userWithEmail = usersRepo.findByEmail(request.getEmailId());

            if (userWithEmail != null &&
                    (userWithEmail.getOrganizations() == null ||
                            userWithEmail.getOrganizations().getOrganizationId() != existing.getOrganizationId())) {

                throw new ValidationException("Email already in use");
            }

            existing.setEmailId(request.getEmailId());
        }

        if (hasText(request.getOrganizationName())) {
            existing.setFullName(request.getOrganizationName());
        }
        if (hasText(request.getLegalBusinessName())) {
            existing.setLegalBusinessName(request.getLegalBusinessName());
        }
        if (hasText(request.getOrganizationType())) {
            existing.setOrganizationType(request.getOrganizationType());
        }
        if (hasText(request.getIndustry())) {
            existing.setIndustry(request.getIndustry());
        }
        if (hasText(request.getCompanySize())) {
            existing.setCompanySize(request.getCompanySize());
        }
        if (hasText(request.getWebsite())) {
            existing.setWebsite(request.getWebsite());
        }
        if (hasText(request.getDescription())) {
            existing.setAboutOrganization(request.getDescription());
        }
        if (hasText(request.getEmailIdAlternate())) {
            existing.setEmailIdAlternate(request.getEmailIdAlternate());
        }
        if (hasText(request.getContact())) {
            existing.setContact(request.getContact());
        }
        if (hasText(request.getContactAlternate())) {
            existing.setContactAlternate(request.getContactAlternate());
        }

        organizationsRepo.save(existing);

        OrganizationAddress address = organizationAddressRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseGet(() -> {
                    OrganizationAddress newAddress = new OrganizationAddress();
                    newAddress.setOrganization(existing);
                    return newAddress;
                });

        if (hasText(request.getAddressLine1())) address.setAddressLine1(request.getAddressLine1());
        if (hasText(request.getAddressLine2())) address.setAddressLine2(request.getAddressLine2());
        if (hasText(request.getCountry())) address.setCountry(request.getCountry());
        if (hasText(request.getState())) address.setState(request.getState());
        if (hasText(request.getCity())) address.setCity(request.getCity());
        if (hasText(request.getPincode())) address.setPincode(request.getPincode());
        organizationAddressRepo.save(address);
        existing.setAddress(address);

        OrganizationSettings settings = organizationSettingsRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseGet(() -> {
                    OrganizationSettings newSettings = new OrganizationSettings();
                    newSettings.setOrganization(existing);
                    return newSettings;
                });

        if (hasText(request.getTimezone())) settings.setTimezone(request.getTimezone());
        if (hasText(request.getLanguage())) settings.setLanguage(request.getLanguage());
        if (hasText(request.getCurrency())) settings.setCurrency(request.getCurrency());
        if (hasText(request.getDefaultTheme())) settings.setDefaultTheme(request.getDefaultTheme());
        organizationSettingsRepo.save(settings);
        existing.setSettings(settings);

        OrganizationBranding branding = organizationBrandingRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseGet(() -> {
                    OrganizationBranding newBranding = new OrganizationBranding();
                    newBranding.setOrganization(existing);
                    return newBranding;
                });

        if (hasText(request.getPrimaryColor())) branding.setPrimaryColor(request.getPrimaryColor());
        if (hasText(request.getSecondaryColor())) branding.setSecondaryColor(request.getSecondaryColor());
        organizationBrandingRepo.save(branding);
        existing.setBranding(branding);

        return mapToDetailsResponse(existing);
    }

    @Override
    public Page<OrganizationListResponse> getOrganizations(
            String search, String approvalStatus, int page, int size, String sortBy, String direction) {

        List<String> allowedSortFields = List.of("organizationId", "fullName", "createdDate");
        String safeSortBy = allowedSortFields.contains(sortBy) ? sortBy : "organizationId";
        Sort.Direction safeDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(safeDirection, safeSortBy));

        ApprovalStatus statusFilter = null;
        if (hasText(approvalStatus)) {
            try {
                statusFilter = ApprovalStatus.valueOf(approvalStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid approval status: " + approvalStatus);
            }
        }

        Page<Organizations> organizationsPage =
                organizationsRepo.searchOrganizations(search, statusFilter, pageable);

        List<OrganizationListResponse> content = organizationsPage.getContent().stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, organizationsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationDetailsResponse getOrganizationDetails(long organizationId) {

        SecurityUtil.assertCanManageOrganization(organizationId);

        Organizations org = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        return mapToDetailsResponse(org);
    }

    @Override
    public List<OrganizationPublicResponse> getPublicOrganizationsList() {

        return organizationsRepo.searchOrganizations(null, ApprovalStatus.APPROVED, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(Organizations::isStatus)
                .map(org -> new OrganizationPublicResponse(
                        org.getOrganizationId(),
                        org.getFullName(),
                        org.getAboutOrganization(),
                        org.getLogo() != null && org.getLogo().length > 0,
                        org.isStatus()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrganizationListResponse> getPendingOrganizations(String search, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<Organizations> organizationsPage =
                organizationsRepo.searchOrganizations(search, ApprovalStatus.PENDING, pageable);

        List<OrganizationListResponse> content = organizationsPage.getContent().stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, organizationsPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrganizationDetailsResponse approveOrganization(long organizationId, OrganizationApprovalRequest request) {

        Organizations org = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        OrganizationApproval approval = organizationApprovalRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Approval record not found"));

        approval.setApprovalStatus(ApprovalStatus.APPROVED);
        approval.setRemarks(request.getRemarks());
        approval.setApprovedBy(SecurityUtil.getCurrentUserEmail());
        approval.setApprovedDate(new Date());
        organizationApprovalRepo.save(approval);

        // Reuses the existing cascade so the org's admin (and any other org
        // users/groups/courses) become active along with the organization itself
        toggleOrganizationStatus(organizationId, true);
        org.setStatus(true);

        return mapToDetailsResponse(org);
    }

    @Override
    @Transactional
    public OrganizationDetailsResponse rejectOrganization(long organizationId, OrganizationApprovalRequest request) {

        Organizations org = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        OrganizationApproval approval = organizationApprovalRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Approval record not found"));

        approval.setApprovalStatus(ApprovalStatus.REJECTED);
        approval.setRemarks(request.getRemarks());
        approval.setRejectedBy(SecurityUtil.getCurrentUserEmail());
        approval.setRejectedDate(new Date());
        organizationApprovalRepo.save(approval);

        toggleOrganizationStatus(organizationId, false);
        org.setStatus(false);

        return mapToDetailsResponse(org);
    }

    @Override
    @Transactional
    public OrganizationDetailsResponse updateOrganizationSettings(long organizationId, OrganizationSettingsRequest request) {

        Organizations org = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        OrganizationSettings settings = organizationSettingsRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseGet(() -> {
                    OrganizationSettings newSettings = new OrganizationSettings();
                    newSettings.setOrganization(org);
                    return newSettings;
                });

        if (request.getMaximumConcurrentSessions() != null) settings.setMaximumConcurrentSessions(request.getMaximumConcurrentSessions());

        if (request.getAllowSelfRegistration() != null) settings.setAllowSelfRegistration(request.getAllowSelfRegistration());
        if (request.getAllowTrainerRegistration() != null) settings.setAllowTrainerRegistration(request.getAllowTrainerRegistration());
        if (request.getAllowExternalExaminee() != null) settings.setAllowExternalExaminee(request.getAllowExternalExaminee());

        organizationSettingsRepo.save(settings);
        org.setSettings(settings);

        // Plan changes go through the dedicated subscription APIs
        // (POST /subscription-plans/assign for ADMIN overrides, or the org's own
        // upgrade/downgrade endpoints). Active/inactive status is managed via the
        // existing PUT /org/toggleStatus/{organizationId}.

        return mapToDetailsResponse(org);
    }

    @Override
    @Transactional
    public void softDeleteOrganization(long organizationId) {
        toggleOrganizationStatus(organizationId, false);
    }

    @Override
    @Transactional
    public OrganizationDetailsResponse uploadBrandingAsset(long organizationId, String assetType, MultipartFile file) {

        Organizations org = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        OrganizationBranding branding = organizationBrandingRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseGet(() -> {
                    OrganizationBranding newBranding = new OrganizationBranding();
                    newBranding.setOrganization(org);
                    return newBranding;
                });

        boolean isFavicon = "favicon".equalsIgnoreCase(assetType);
        boolean isBanner = "banner".equalsIgnoreCase(assetType);

        if (!isFavicon && !isBanner) {
            throw new ValidationException("Invalid asset type. Allowed: banner, favicon");
        }

        FileUtils.validateFile(file, FileFormats.userProfilePictureFormat(), isFavicon ? 5 : 15);

        String contentType = FileUtils.resolveContentType(file.getOriginalFilename(), file.getContentType());

        try {
            byte[] compressed = FileUtils.compressFile(file.getBytes());

            if (isBanner) {
                branding.setBannerFileName(file.getOriginalFilename());
                branding.setBannerFileType(contentType);
                branding.setBanner(compressed);
            } else {
                branding.setFaviconFileName(file.getOriginalFilename());
                branding.setFaviconFileType(contentType);
                branding.setFavicon(compressed);
            }
        } catch (IOException e) {
            throw new ValidationException("Unable to upload " + assetType + ": " + e.getMessage());
        }

        organizationBrandingRepo.save(branding);
        org.setBranding(branding);

        return mapToDetailsResponse(org);
    }

    @Override
    public MediaResponse getBrandingAsset(long organizationId, String assetType) {

        OrganizationBranding branding = organizationBrandingRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Branding not found for this organization"));

        byte[] data;
        String contentType;

        if ("banner".equalsIgnoreCase(assetType)) {
            data = branding.getBanner();
            contentType = branding.getBannerFileType();
        } else if ("favicon".equalsIgnoreCase(assetType)) {
            data = branding.getFavicon();
            contentType = branding.getFaviconFileType();
        } else {
            throw new ValidationException("Invalid asset type. Allowed: banner, favicon");
        }

        if (data == null || data.length == 0) {
            throw new ValidationException("No " + assetType + " found for this organization");
        }

        return new MediaResponse(data, contentType);
    }

    @Override
    @Transactional
    public OrganizationDetailsResponse uploadDocument(long organizationId, String documentType, MultipartFile file) {

        Organizations org = organizationsRepo.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        OrganizationDocuments documents = organizationDocumentsRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseGet(() -> {
                    OrganizationDocuments newDocuments = new OrganizationDocuments();
                    newDocuments.setOrganization(org);
                    return newDocuments;
                });

        switch (documentType) {
            case "certificateOfIncorporation" -> attachDocument(
                    documents::setCertificateOfIncorporationFile,
                    documents::setCertificateOfIncorporationFileName,
                    documents::setCertificateOfIncorporationFileType,
                    file);
            case "gstCertificate" -> attachDocument(
                    documents::setGstCertificateFile,
                    documents::setGstCertificateFileName,
                    documents::setGstCertificateFileType,
                    file);
            case "panCard" -> attachDocument(
                    documents::setPanCardFile,
                    documents::setPanCardFileName,
                    documents::setPanCardFileType,
                    file);
            case "msmeCertificate" -> attachDocument(
                    documents::setMsmeCertificateFile,
                    documents::setMsmeCertificateFileName,
                    documents::setMsmeCertificateFileType,
                    file);
            case "isoCertificate" -> attachDocument(
                    documents::setIsoCertificateFile,
                    documents::setIsoCertificateFileName,
                    documents::setIsoCertificateFileType,
                    file);
            default -> throw new ValidationException(
                    "Invalid document type. Allowed: certificateOfIncorporation, gstCertificate, panCard, msmeCertificate, isoCertificate"
            );
        }

        organizationDocumentsRepo.save(documents);
        org.setDocuments(documents);

        return mapToDetailsResponse(org);
    }

    @Override
    public MediaResponse getDocument(long organizationId, String documentType) {

        OrganizationDocuments documents = organizationDocumentsRepo
                .findByOrganization_OrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Documents not found for this organization"));

        byte[] data;
        String contentType;

        switch (documentType) {
            case "certificateOfIncorporation" -> {
                data = documents.getCertificateOfIncorporationFile();
                contentType = documents.getCertificateOfIncorporationFileType();
            }
            case "gstCertificate" -> {
                data = documents.getGstCertificateFile();
                contentType = documents.getGstCertificateFileType();
            }
            case "panCard" -> {
                data = documents.getPanCardFile();
                contentType = documents.getPanCardFileType();
            }
            case "msmeCertificate" -> {
                data = documents.getMsmeCertificateFile();
                contentType = documents.getMsmeCertificateFileType();
            }
            case "isoCertificate" -> {
                data = documents.getIsoCertificateFile();
                contentType = documents.getIsoCertificateFileType();
            }
            default -> throw new ValidationException(
                    "Invalid document type. Allowed: certificateOfIncorporation, gstCertificate, panCard, msmeCertificate, isoCertificate"
            );
        }

        if (data == null || data.length == 0) {
            throw new ValidationException("No " + documentType + " found for this organization");
        }

        return new MediaResponse(data, contentType);
    }
}
