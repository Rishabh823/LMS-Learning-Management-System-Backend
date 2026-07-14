package com.cipherinfratech.lms.subscription.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyPaymentRequest {

    @NotBlank(message = "orderId is required")
    private String orderId;

    @NotBlank(message = "paymentId is required")
    private String paymentId;

    @NotBlank(message = "signature is required")
    private String signature;

    @NotNull(message = "Plan id is required")
    private Long planId;

    @Valid
    @NotNull(message = "Organization details are required")
    private OrganizationInfoDTO organization;

    @Valid
    @NotNull(message = "Admin details are required")
    private AdminInfoDTO admin;

}
