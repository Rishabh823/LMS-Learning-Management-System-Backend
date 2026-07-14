package com.cipherinfratech.lms.organizations.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadOrganizationLogoRequest {

    @NotNull(message = "Organization ID is required")
    private Long organizationId;

    @NotNull(message = "Logo file is required")
    private MultipartFile logo;
}
