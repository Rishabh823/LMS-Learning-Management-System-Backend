package com.cipherinfratech.lms.dashboard.dto;

import com.cipherinfratech.lms.users.entity.Roles;
import lombok.Data;

import java.util.List;

@Data
public class PageResponse {

    private Long id;

    private String pageName;

    private String pageUrl;

    private Integer position;

    private Boolean status;

    private Long sectionId;

    private String sectionName;

    private List<Roles> roles;

}