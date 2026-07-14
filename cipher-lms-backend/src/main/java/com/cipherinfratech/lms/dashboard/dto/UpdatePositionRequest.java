package com.cipherinfratech.lms.dashboard.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePositionRequest {

    @NotNull(message = "Id is required")
    private Long id;

    @NotNull(message = "Position is required")
    private Integer position;
}