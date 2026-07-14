package com.cipherinfratech.lms.dashboard.service;

import com.cipherinfratech.lms.dashboard.dto.SidebarDTO;
import com.cipherinfratech.lms.dashboard.dto.SidebarProjection;
import com.cipherinfratech.lms.dashboard.dto.SidebarUserProjection;
import com.cipherinfratech.lms.dashboard.repository.PageRepo;
import com.cipherinfratech.lms.handlers.NotFoundException;
import com.cipherinfratech.lms.users.entity.Roles;
import com.cipherinfratech.lms.users.repositories.UsersRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SidebarServiceImpl implements SidebarService{

    private final UsersRepo usersRepo;
    private final PageRepo pageRepo;

    public SidebarDTO getSidebar(Principal principal) {

        SidebarUserProjection user =
                usersRepo.findSidebarUserByEmail(principal.getName());

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        List<SidebarProjection> rows;

        if (user.getRole() == Roles.ADMIN) {
            rows = pageRepo.findAllForAdmin();   // NO PageAccess check
            log.debug("Admin detected, by passed page access checks");
        } else {
            rows = pageRepo.findSidebarByRole(user.getRole());
            log.debug("Non-admin detected, fetched page access");

        }

        Map<Long, SidebarDTO.ModuleDTO> moduleMap = new LinkedHashMap<>();

        for (SidebarProjection row : rows) {

            SidebarDTO.ModuleDTO module =
                    moduleMap.computeIfAbsent(row.getModuleId(), id -> {
                        SidebarDTO.ModuleDTO m = new SidebarDTO.ModuleDTO();
                        m.setModuleId(row.getModuleId());
                        m.setModuleName(row.getModuleName());
                        m.setModulePosition(row.getModulePosition());
                        m.setPages(new ArrayList<>());
                        return m;
                    });

            SidebarDTO.PageDTO page = new SidebarDTO.PageDTO();
            page.setPageId(row.getPageId());
            page.setPageName(row.getPageName());
            page.setPageUrl(row.getPageUrl());
            page.setPagePosition(row.getPagePosition());

            module.getPages().add(page);
        }

        SidebarDTO response = new SidebarDTO();

        response.setUserId(user.getUserId());
        response.setFirstName(user.getName());
        response.setMiddleName(null);
        response.setLastName(null);
        response.setEmail(user.getEmailId());
        response.setRole(user.getRole().name());

        response.setModules(new ArrayList<>(moduleMap.values()));

        return response;
    }
}