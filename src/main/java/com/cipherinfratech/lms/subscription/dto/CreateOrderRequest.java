package com.cipherinfratech.lms.subscription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Plan id is required")
    private Long planId;

}
