package com.cipherinfratech.lms.dashboard.service;

import com.cipherinfratech.lms.dashboard.dto.*;
import com.cipherinfratech.lms.dashboard.entities.Page;
import com.cipherinfratech.lms.dashboard.entities.PageAccess;
import com.cipherinfratech.lms.dashboard.entities.Section;
import com.cipherinfratech.lms.dashboard.repository.PageAccessRepo;
import com.cipherinfratech.lms.dashboard.repository.PageRepo;
import com.cipherinfratech.lms.dashboard.repository.SectionRepo;
import com.cipherinfratech.lms.handlers.ValidationException;
import com.cipherinfratech.lms.users.entity.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PageServiceImpl implements PageService {

    private final PageRepo pageRepo;
    private final SectionRepo sectionRepo;
    private final PageAccessRepo pageAccessRepo;

    @Override
    @Transactional
    public void addPage(AddPageOnlyRequest request) {

        Section section = sectionRepo.findById(request.getSectionId())
                .orElseThrow(() ->
                        new ValidationException("Section not found"));

        if (pageRepo.existsByUrl(request.getPageUrl())) {
            throw new ValidationException("Page URL already exists");
        }

        if (request.getRoles().contains(Roles.ADMIN)) {
            throw new ValidationException("ADMIN role cannot be assigned explicitly");
        }

        Integer maxPosition =
                pageRepo.getMaxPositionBySectionId(section.getId());

        Page page = new Page();
        page.setName(request.getPageName());
        page.setUrl(request.getPageUrl());
        page.setSection(section);
        page.setStatus(true);
        page.setPosition(maxPosition + 1);

        page = pageRepo.save(page);

        for (Roles role : request.getRoles()) {

            PageAccess access = new PageAccess();
            access.setPage(page);
            access.setRole(role);

            pageAccessRepo.save(access);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getPage(Long pageId) {

        Page page = pageRepo.findById(pageId)
                .orElseThrow(() ->
                        new ValidationException("Page not found"));

        List<Roles> roles =
                pageAccessRepo.findRolesByPageId(page.getId());

        PageResponse response = new PageResponse();

        response.setId(page.getId());
        response.setPageName(page.getName());
        response.setPageUrl(page.getUrl());
        response.setPosition(page.getPosition());
        response.setStatus(page.isStatus());

        response.setSectionId(page.getSection().getId());
        response.setSectionName(page.getSection().getName());

        response.setRoles(roles);

        return response;
    }

    @Override
    @Transactional
    public void updatePage(Long pageId, UpdatePageRequest request) {

        Page page = pageRepo.findById(pageId)
                .orElseThrow(() ->
                        new ValidationException("Page not found"));

        /*
         * Update Page Name
         */
        if (request.getPageName() != null &&
                !request.getPageName().trim().isEmpty()) {

            page.setName(request.getPageName().trim());
        }

        /*
         * Update URL
         */
        if (request.getPageUrl() != null &&
                !request.getPageUrl().trim().isEmpty()) {

            if (pageRepo.existsByUrlAndIdNot(request.getPageUrl(), pageId)) {
                throw new ValidationException("Page URL already exists");
            }

            page.setUrl(request.getPageUrl().trim());
        }

        /*
         * Update Status
         */
        if (request.getStatus() != null) {
            page.setStatus(request.getStatus());
        }

        /*
         * Change Section
         */
        if (request.getSectionId() != null &&
                !request.getSectionId().equals(page.getSection().getId())) {

            Section newSection = sectionRepo.findById(request.getSectionId())
                    .orElseThrow(() ->
                            new ValidationException("Section not found"));

            Integer maxPosition =
                    pageRepo.getMaxPositionBySectionId(newSection.getId());

            page.setSection(newSection);
            page.setPosition(maxPosition + 1);
        }

        pageRepo.save(page);

        /*
         * Update Roles
         */
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {

            if (request.getRoles().contains(Roles.ADMIN)) {
                throw new ValidationException(
                        "ADMIN role cannot be assigned explicitly");
            }

            // Existing Roles
            List<Roles> existingRoles =
                    pageAccessRepo.findRolesByPageId(page.getId());

            // If roles are exactly same, don't touch page_access table
            if (existingRoles.size() == request.getRoles().size()
                    && existingRoles.containsAll(request.getRoles())
                    && request.getRoles().containsAll(existingRoles)) {

                return;
            }

            // Delete old roles
            pageAccessRepo.deleteByPage(page);

            // Insert only missing roles
            for (Roles role : request.getRoles()) {

                if (pageAccessRepo.existsByPageAndRole(page, role)) {
                    continue;
                }

                PageAccess access = new PageAccess();
                access.setPage(page);
                access.setRole(role);

                pageAccessRepo.save(access);
            }
        }
    }

    @Override
    @Transactional
    public void deletePage(Long pageId) {

        Page page = pageRepo.findById(pageId)
                .orElseThrow(() ->
                        new ValidationException(
                                "Page not found"));

        /*
         * Delete all page access entries
         */
        pageAccessRepo.deleteByPage(page);

        /*
         * Delete page
         */
        pageRepo.delete(page);

    }

    @Override
    @Transactional
    public void updatePagePosition(Long sectionId,
                                   List<UpdatePositionRequest> request) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() ->
                        new ValidationException("Section not found"));

        List<Page> pages = pageRepo.findBySectionIdOrderByPosition(section.getId());

        if (pages.size() != request.size()) {
            throw new ValidationException("Invalid page ordering request");
        }

        for (UpdatePositionRequest item : request) {

            Page page = pageRepo.findById(item.getId())
                    .orElseThrow(() ->
                            new ValidationException("Page not found"));

            if (!page.getSection().getId().equals(sectionId)) {
                throw new ValidationException(
                        "Page does not belong to selected section");
            }

            page.setPosition(item.getPosition());

            pageRepo.save(page);
        }

    }

    @Override
    @Transactional
    public void updatePageStatus(Long pageId) {

        Page page = pageRepo.findById(pageId)
                .orElseThrow(() ->
                        new ValidationException("Page not found"));

        page.setStatus(!page.isStatus());

        pageRepo.save(page);

    }
}