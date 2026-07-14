package com.cipherinfratech.lms.dashboard.dto;

import com.cipherinfratech.lms.users.entity.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddPageRequest {

    @NotBlank(message = "Page name cant be blank")
    private String pageName;

    @NotBlank(message = "Page url cant be blank")
    private String pageUrl;

    @NotEmpty(message = "Roles cant be empty")
    private List<Roles> roles;
}
