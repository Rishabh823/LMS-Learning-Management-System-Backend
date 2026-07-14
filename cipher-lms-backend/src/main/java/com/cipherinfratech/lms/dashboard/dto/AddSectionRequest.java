package com.cipherinfratech.lms.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddSectionRequest {

    @NotBlank(message = "Section name cant be blank")
    private String sectionName;

    @NotEmpty(message = "Pages cant be empty")
    private List<AddPageRequest> pages = new ArrayList<>();

}
