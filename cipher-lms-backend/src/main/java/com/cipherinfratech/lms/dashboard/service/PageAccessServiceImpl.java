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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageAccessServiceImpl implements PageAccessService {

    private final SectionRepo sectionRepo;
    private final PageRepo pageRepo;
    private final PageAccessRepo pageAccessRepo;

    @Transactional
    @Override
    public void addSection(AddSectionRequest request) {

        Section section;

        // CASE 1: Section exists
        if (sectionRepo.existsByName(request.getSectionName())) {

            section = sectionRepo.findByName(request.getSectionName())
                    .orElseThrow(() -> new ValidationException("Section not found"));

        } else {

            // Create new section with position
            Integer maxPosition = sectionRepo.getMaxPosition();

            section = new Section();
            section.setName(request.getSectionName());
            section.setPosition(maxPosition + 1);

            section = sectionRepo.save(section);
        }

        boolean allPagesAndRolesExist = true;

        for (AddPageRequest pageRequest : request.getPages()) {

            if (pageRequest.getRoles().contains(Roles.ADMIN)) {
                throw new ValidationException("ADMIN role should not be assigned explicitly");
            }

            Page page;

            // CASE 2: Page exists
            if (pageRepo.existsByUrl(pageRequest.getPageUrl())) {

                page = pageRepo.findByUrl(pageRequest.getPageUrl())
                        .orElseThrow(() -> new ValidationException("Page not found"));

            } else {

                // New page → assign position within section
                Integer maxPagePosition = pageRepo.getMaxPositionBySectionId(section.getId());

                page = new Page();
                page.setName(pageRequest.getPageName());
                page.setUrl(pageRequest.getPageUrl());
                page.setSection(section);
                page.setPosition(maxPagePosition + 1);

                page = pageRepo.save(page);

                allPagesAndRolesExist = false; // new page created
            }

            // ROLE CHECK
            List<Roles> existingRoles = pageAccessRepo.findRolesByPageId(page.getId());

            for (Roles role : pageRequest.getRoles()) {

                if (!existingRoles.contains(role)) {

                    PageAccess access = new PageAccess();
                    access.setPage(page);
                    access.setRole(role);

                    pageAccessRepo.save(access);

                    allPagesAndRolesExist = false; // new role added
                }
            }
        }

        // FINAL CHECK
        if (allPagesAndRolesExist) {
            throw new ValidationException("All pages and roles already exist for this section");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SectionResponse> getAllSections() {

        List<Section> sections = sectionRepo.findAll();

        sections.sort((s1, s2) ->
                Integer.compare(s1.getPosition(), s2.getPosition()));

        List<SectionResponse> responseList = new ArrayList<>();

        for (Section section : sections) {

            SectionResponse response = new SectionResponse();

            response.setId(section.getId());
            response.setName(section.getName());
            response.setPosition(section.getPosition());
            response.setStatus(section.isStatus());

            List<Page> pages =
                    pageRepo.findBySectionIdOrderByPosition(section.getId());

            List<PageResponse> pageResponses = new ArrayList<>();

            for (Page page : pages) {

                PageResponse pageResponse = new PageResponse();

                pageResponse.setId(page.getId());
                pageResponse.setPageName(page.getName());
                pageResponse.setPageUrl(page.getUrl());
                pageResponse.setPosition(page.getPosition());
                pageResponse.setStatus(page.isStatus());

                pageResponse.setSectionId(section.getId());
                pageResponse.setSectionName(section.getName());

                pageResponse.setRoles(
                        pageAccessRepo.findRolesByPageId(page.getId())
                );

                pageResponses.add(pageResponse);
            }

            response.setPages(pageResponses);

            responseList.add(response);
        }

        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public SectionResponse getSection(Long sectionId) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() ->
                        new ValidationException("Section not found"));

        SectionResponse response = new SectionResponse();

        response.setId(section.getId());
        response.setName(section.getName());
        response.setPosition(section.getPosition());
        response.setStatus(section.isStatus());

        List<Page> pages =
                pageRepo.findBySectionIdOrderByPosition(sectionId);

        List<PageResponse> pageResponses = new ArrayList<>();

        for (Page page : pages) {

            PageResponse pageResponse = new PageResponse();

            pageResponse.setId(page.getId());
            pageResponse.setPageName(page.getName());
            pageResponse.setPageUrl(page.getUrl());
            pageResponse.setPosition(page.getPosition());
            pageResponse.setStatus(page.isStatus());

            pageResponse.setSectionId(section.getId());
            pageResponse.setSectionName(section.getName());

            pageResponse.setRoles(
                    pageAccessRepo.findRolesByPageId(page.getId())
            );

            pageResponses.add(pageResponse);
        }

        response.setPages(pageResponses);

        return response;
    }

    @Override
    @Transactional
    public void updateSection(Long sectionId,
                              UpdateSectionRequest request) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() ->
                        new ValidationException("Section not found"));

        if (request.getName() != null &&
                !request.getName().trim().isEmpty()) {

            if (sectionRepo.existsByNameAndIdNot(
                    request.getName(),
                    sectionId)) {

                throw new ValidationException(
                        "Section name already exists");
            }

            section.setName(request.getName().trim());
        }

        if (request.getStatus() != null) {
            section.setStatus(request.getStatus());
        }

        sectionRepo.save(section);
    }

    @Override
    @Transactional
    public void deleteSection(Long sectionId) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() ->
                        new ValidationException("Section not found"));

        List<Page> pages =
                pageRepo.findBySectionIdOrderByPosition(sectionId);

        for (Page page : pages) {

            pageAccessRepo.deleteByPage(page);

            pageRepo.delete(page);
        }

        sectionRepo.delete(section);
    }

    @Override
    @Transactional
    public void updateSectionPosition(
            List<UpdatePositionRequest> request) {

        List<Section> sections = sectionRepo.findAll();

        if (sections.size() != request.size()) {
            throw new ValidationException(
                    "Invalid section ordering request");
        }

        for (UpdatePositionRequest item : request) {

            Section section = sectionRepo.findById(item.getId())
                    .orElseThrow(() ->
                            new ValidationException("Section not found"));

            section.setPosition(item.getPosition());

            sectionRepo.save(section);
        }

    }

    @Override
    @Transactional
    public void updateSectionStatus(Long sectionId) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() ->
                        new ValidationException("Section not found"));

        section.setStatus(!section.isStatus());

        sectionRepo.save(section);

    }

}
