package com.cipherinfratech.lms.organizations.services;

import com.cipherinfratech.lms.organizations.dto.*;
import com.cipherinfratech.lms.organizations.entities.Organizations;
import com.cipherinfratech.lms.utils.MediaResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrganizationsService {
    List<Organizations> getAll();

    void uploadLogo(UploadOrganizationLogoRequest request) throws IOException;

    MediaResponse getOrganizationLogo(Long organizationId);

    void toggleOrganizationStatus(Long organizationId, boolean activate);

    // ---- Organization Onboarding ----

    OrganizationDetailsResponse onboardOrganization(
            @Valid OrganizationOnboardingRequest request,
            MultipartFile logo,
            MultipartFile banner,
            MultipartFile favicon,
            MultipartFile certificateOfIncorporation,
            MultipartFile gstCertificate,
            MultipartFile panCard,
            MultipartFile msmeCertificate,
            MultipartFile isoCertificate
    );

    OrganizationDetailsResponse updateOrganizationDetails(
            long organizationId,
            OrganizationUpdateRequest request
    );

    Page<OrganizationListResponse> getOrganizations(
            String search,
            String approvalStatus,
            int page,
            int size,
            String sortBy,
            String direction
    );

    OrganizationDetailsResponse getOrganizationDetails(long organizationId);

    List<OrganizationPublicResponse> getPublicOrganizationsList();

    Page<OrganizationListResponse> getPendingOrganizations(String search, int page, int size);

    OrganizationDetailsResponse approveOrganization(long organizationId, OrganizationApprovalRequest request);

    OrganizationDetailsResponse rejectOrganization(long organizationId, OrganizationApprovalRequest request);

    OrganizationDetailsResponse updateOrganizationSettings(long organizationId, OrganizationSettingsRequest request);

    void softDeleteOrganization(long organizationId);

    OrganizationDetailsResponse uploadBrandingAsset(long organizationId, String assetType, MultipartFile file);

    MediaResponse getBrandingAsset(long organizationId, String assetType);

    OrganizationDetailsResponse uploadDocument(long organizationId, String documentType, MultipartFile file);

    MediaResponse getDocument(long organizationId, String documentType);
}
