package com.cipherinfratech.lms.organizations.controllers;

import com.cipherinfratech.lms.organizations.dto.*;
import com.cipherinfratech.lms.organizations.services.OrganizationsService;
import com.cipherinfratech.lms.subscription.dto.OrganizationRegisterRequest;
import com.cipherinfratech.lms.subscription.services.OrganizationRegistrationService;
import com.cipherinfratech.lms.utils.MediaResponse;
import com.cipherinfratech.lms.utils.ResponseModels;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/organizations")
@AllArgsConstructor
public class OrganizationOnboardingController {

    private OrganizationsService organizationsService;
    private OrganizationRegistrationService organizationRegistrationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> onboardOrganization(
            @Valid @RequestPart("data") OrganizationOnboardingRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "banner", required = false) MultipartFile banner,
            @RequestPart(value = "favicon", required = false) MultipartFile favicon,
            @RequestPart(value = "certificateOfIncorporation", required = false) MultipartFile certificateOfIncorporation,
            @RequestPart(value = "gstCertificate", required = false) MultipartFile gstCertificate,
            @RequestPart(value = "panCard", required = false) MultipartFile panCard,
            @RequestPart(value = "msmeCertificate", required = false) MultipartFile msmeCertificate,
            @RequestPart(value = "isoCertificate", required = false) MultipartFile isoCertificate
    ) {

        OrganizationDetailsResponse response = organizationsService.onboardOrganization(
                request, logo, banner, favicon,
                certificateOfIncorporation, gstCertificate, panCard, msmeCertificate, isoCertificate
        );

        return ResponseModels.createWithPayload(
                "Organization onboarded successfully. Pending admin approval.",
                response
        );
    }

    /**
     * Plan-based self-serve signup (public, no auth). Only for the FREE plan -
     * paid plans go through POST /payments/create-order + /payments/verify instead.
     * Unlike the rich onboarding endpoint above, this activates the organization
     * and admin immediately (no approval step) and returns a JWT, like /login.
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerOrganization(@Valid @RequestBody OrganizationRegisterRequest request) {

        return ResponseModels.createWithPayload(
                "Organization registered successfully",
                organizationRegistrationService.registerFreePlan(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PutMapping("/{organizationId}")
    public ResponseEntity<Object> updateOrganization(
            @PathVariable long organizationId,
            @RequestBody OrganizationUpdateRequest request) {

        OrganizationDetailsResponse response =
                organizationsService.updateOrganizationDetails(organizationId, request);

        return ResponseModels.successWithPayload(
                "Organization updated successfully",
                response
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<Object> getOrganizations(@RequestBody OrganizationSearchRequest request) {

        Page<OrganizationListResponse> organizationsPage = organizationsService.getOrganizations(
                request.getSearch(), request.getApprovalStatus(), request.getPage(), request.getSize(),
                request.getSortBy(), request.getDirection()
        );

        return ResponseModels.successWithPayloadPaginated(
                "Organizations retrieved successfully",
                organizationsPage
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @GetMapping("/{organizationId}")
    public ResponseEntity<Object> getOrganizationDetails(@PathVariable long organizationId) {

        return ResponseModels.successWithPayload(
                "Organization details fetched successfully",
                organizationsService.getOrganizationDetails(organizationId)
        );
    }

    @GetMapping("/public")
    public ResponseEntity<Object> getPublicOrganizations() {

        return ResponseModels.successWithPayload(
                "Organizations fetched successfully",
                organizationsService.getPublicOrganizationsList()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pending")
    public ResponseEntity<Object> getPendingOrganizations(@RequestBody OrganizationSearchRequest request) {

        Page<OrganizationListResponse> pendingPage = organizationsService.getPendingOrganizations(
                request.getSearch(), request.getPage(), request.getSize()
        );

        return ResponseModels.successWithPayloadPaginated(
                "Pending organizations retrieved successfully",
                pendingPage
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{organizationId}/approve")
    public ResponseEntity<Object> approveOrganization(
            @PathVariable long organizationId,
            @RequestBody OrganizationApprovalRequest request) {

        OrganizationDetailsResponse response =
                organizationsService.approveOrganization(organizationId, request);

        return ResponseModels.successWithPayload(
                "Organization approved successfully",
                response
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{organizationId}/reject")
    public ResponseEntity<Object> rejectOrganization(
            @PathVariable long organizationId,
            @RequestBody OrganizationApprovalRequest request) {

        OrganizationDetailsResponse response =
                organizationsService.rejectOrganization(organizationId, request);

        return ResponseModels.successWithPayload(
                "Organization rejected successfully",
                response
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{organizationId}/settings")
    public ResponseEntity<Object> updateOrganizationSettings(
            @PathVariable long organizationId,
            @RequestBody OrganizationSettingsRequest request) {

        OrganizationDetailsResponse response =
                organizationsService.updateOrganizationSettings(organizationId, request);

        return ResponseModels.successWithPayload(
                "Organization settings updated successfully",
                response
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{organizationId}")
    public ResponseEntity<Object> deleteOrganization(@PathVariable long organizationId) {

        organizationsService.softDeleteOrganization(organizationId);

        return ResponseModels.deleted("Organization deleted successfully");
    }

    // ---- Logo (reuses the existing Organizations.logo field / service methods) ----

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PostMapping(value = "/{organizationId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadLogo(
            @PathVariable long organizationId,
            @RequestPart("file") MultipartFile file) throws IOException {

        UploadOrganizationLogoRequest request = new UploadOrganizationLogoRequest();
        request.setOrganizationId(organizationId);
        request.setLogo(file);

        organizationsService.uploadLogo(request);

        return ResponseModels.successWithPayload(
                "Logo uploaded successfully",
                organizationsService.getOrganizationDetails(organizationId)
        );
    }

    @GetMapping("/{organizationId}/logo")
    public ResponseEntity<Object> getLogo(@PathVariable long organizationId) {

        MediaResponse media = organizationsService.getOrganizationLogo(organizationId);

        return ResponseModels.sendMediaWithDecompress(media.getData(), media.getContentType());
    }

    // ---- Branding assets: banner, favicon ----

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PostMapping(value = "/{organizationId}/branding/{assetType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadBrandingAsset(
            @PathVariable long organizationId,
            @PathVariable String assetType,
            @RequestPart("file") MultipartFile file) {

        OrganizationDetailsResponse response =
                organizationsService.uploadBrandingAsset(organizationId, assetType, file);

        return ResponseModels.successWithPayload(
                assetType + " uploaded successfully",
                response
        );
    }

    @GetMapping("/{organizationId}/branding/{assetType}")
    public ResponseEntity<Object> getBrandingAsset(
            @PathVariable long organizationId,
            @PathVariable String assetType) {

        MediaResponse media = organizationsService.getBrandingAsset(organizationId, assetType);

        return ResponseModels.sendMediaWithDecompress(media.getData(), media.getContentType());
    }

    // ---- Documents: certificateOfIncorporation, gstCertificate, panCard, msmeCertificate, isoCertificate ----

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @PostMapping(value = "/{organizationId}/documents/{documentType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadDocument(
            @PathVariable long organizationId,
            @PathVariable String documentType,
            @RequestPart("file") MultipartFile file) {

        OrganizationDetailsResponse response =
                organizationsService.uploadDocument(organizationId, documentType, file);

        return ResponseModels.successWithPayload(
                documentType + " uploaded successfully",
                response
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZATION','ORG_ADMIN')")
    @GetMapping("/{organizationId}/documents/{documentType}")
    public ResponseEntity<Object> getDocument(
            @PathVariable long organizationId,
            @PathVariable String documentType) {

        MediaResponse media = organizationsService.getDocument(organizationId, documentType);

        return ResponseModels.sendMediaWithDecompress(media.getData(), media.getContentType());
    }

}
