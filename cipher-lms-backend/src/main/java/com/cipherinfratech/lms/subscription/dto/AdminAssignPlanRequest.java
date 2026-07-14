package com.cipherinfratech.lms.subscription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminAssignPlanRequest {

    @NotNull(message = "Organization id is required")
    private Long organizationId;

    @NotNull(message = "Plan id is required")
    private Long planId;

}
