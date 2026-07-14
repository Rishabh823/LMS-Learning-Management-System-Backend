package com.cipherinfratech.lms.dashboard.dto;

import com.cipherinfratech.lms.users.entity.Roles;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatePageRequest {

    private String pageName;

    private String pageUrl;

    private Long sectionId;

    private Boolean status;

    private List<Roles> roles;
}
