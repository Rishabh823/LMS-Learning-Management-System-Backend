package com.cipherinfratech.lms.dashboard.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SidebarDTO {

    // ✅ User info
    private UUID userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String role;

    // ✅ Sidebar structure
    private List<ModuleDTO> modules;

    @Data
    public static class ModuleDTO {

        // Section → Module
        private Long moduleId;
        private String moduleName;
        private Integer modulePosition;

        private List<PageDTO> pages;
    }

    @Data
    public static class PageDTO {

        private Long pageId;
        private String pageName;
        private String pageUrl;
        private Integer pagePosition;
    }
}