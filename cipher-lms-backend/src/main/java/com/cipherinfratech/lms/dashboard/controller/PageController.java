package com.cipherinfratech.lms.dashboard.controller;

import com.cipherinfratech.lms.dashboard.dto.AddPageOnlyRequest;
import com.cipherinfratech.lms.dashboard.dto.PageResponse;
import com.cipherinfratech.lms.dashboard.dto.UpdatePageRequest;
import com.cipherinfratech.lms.dashboard.dto.UpdatePositionRequest;
import com.cipherinfratech.lms.dashboard.service.PageService;
import com.cipherinfratech.lms.utils.ResponseModels;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/page")
public class PageController {

    private final PageService pageService;

    /**
     * Add page to existing section
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addPage(
            @Valid @RequestBody AddPageOnlyRequest request) {

        pageService.addPage(request);

        return ResponseModels.create(
                "Page created successfully."
        );
    }

    /**
     * Get page by id
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{pageId}")
    public ResponseEntity<?> getPage(
            @PathVariable Long pageId) {

        PageResponse response = pageService.getPage(pageId);

        return ResponseModels.successWithPayload(
                "Page fetched successfully.",
                response
        );
    }

    /**
     * Update page
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{pageId}")
    public ResponseEntity<?> updatePage(
            @PathVariable Long pageId,
            @Valid @RequestBody UpdatePageRequest request) {

        pageService.updatePage(pageId, request);

        return ResponseModels.success(
                "Page updated successfully."
        );
    }

    /**
     * Delete page
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{pageId}")
    public ResponseEntity<?> deletePage(
            @PathVariable Long pageId) {

        pageService.deletePage(pageId);

        return ResponseModels.success(
                "Page deleted successfully."
        );
    }

    /**
     * Update page ordering inside section
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/order/{sectionId}")
    public ResponseEntity<?> updatePagePosition(
            @PathVariable Long sectionId,
            @RequestBody List<UpdatePositionRequest> request) {

        pageService.updatePagePosition(sectionId, request);

        return ResponseModels.success(
                "Page positions updated successfully."
        );
    }

    /**
     * Enable / Disable page
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{pageId}")
    public ResponseEntity<?> updatePageStatus(
            @PathVariable Long pageId) {

        pageService.updatePageStatus(pageId);

        return ResponseModels.success(
                "Page status updated successfully."
        );
    }

}