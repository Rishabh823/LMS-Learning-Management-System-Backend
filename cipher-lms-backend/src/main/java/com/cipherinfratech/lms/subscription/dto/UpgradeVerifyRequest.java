package com.cipherinfratech.lms.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpgradeVerifyRequest {

    @NotBlank(message = "orderId is required")
    private String orderId;

    @NotBlank(message = "paymentId is required")
    private String paymentId;

    @NotBlank(message = "signature is required")
    private String signature;

}
