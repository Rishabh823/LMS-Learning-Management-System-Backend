package com.cipherinfratech.lms.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSectionRequest {

    @NotBlank(message = "Section name is required")
    private String name;

    private Boolean status;
}