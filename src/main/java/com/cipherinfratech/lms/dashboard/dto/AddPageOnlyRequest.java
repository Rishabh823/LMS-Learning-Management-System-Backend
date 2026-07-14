package com.cipherinfratech.lms.dashboard.dto;

import com.cipherinfratech.lms.users.entity.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddPageOnlyRequest {

    @NotNull(message = "Section Id is required")
    private Long sectionId;

    @NotBlank(message = "Page name is required")
    private String pageName;

    @NotBlank(message = "Page url is required")
    private String pageUrl;

    @NotEmpty(message = "Roles are required")
    private List<Roles> roles;
}
