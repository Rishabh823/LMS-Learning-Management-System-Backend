package com.cipherinfratech.lms.dashboard.controller;

import com.cipherinfratech.lms.dashboard.dto.AddSectionRequest;
import com.cipherinfratech.lms.dashboard.dto.SectionResponse;
import com.cipherinfratech.lms.dashboard.dto.UpdatePositionRequest;
import com.cipherinfratech.lms.dashboard.dto.UpdateSectionRequest;
import com.cipherinfratech.lms.dashboard.service.PageAccessService;
import com.cipherinfratech.lms.utils.ResponseModels;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/page-access")
public class PageAccessController {

    private final PageAccessService pageAccessService;

    /**
     * Create new section with pages
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addSection(
            @Valid @RequestBody AddSectionRequest request) {

        pageAccessService.addSection(request);

        return ResponseModels.create(
                "Section created successfully."
        );
    }

    /**
     * Get all sections with pages
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllSections() {

        List<SectionResponse> response =
                pageAccessService.getAllSections();

        return ResponseModels.successWithPayload(
                "Sections fetched successfully.",
                response
        );
    }

    /**
     * Get section by id
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{sectionId}")
    public ResponseEntity<?> getSection(
            @PathVariable Long sectionId) {

        SectionResponse response =
                pageAccessService.getSection(sectionId);

        return ResponseModels.successWithPayload(
                "Section fetched successfully.",
                response
        );
    }

    /**
     * Update section
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/section/{sectionId}")
    public ResponseEntity<?> updateSection(
            @PathVariable Long sectionId,
            @Valid @RequestBody UpdateSectionRequest request) {

        pageAccessService.updateSection(
                sectionId,
                request
        );

        return ResponseModels.success(
                "Section updated successfully."
        );
    }

    /**
     * Delete section
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/section/{sectionId}")
    public ResponseEntity<?> deleteSection(
            @PathVariable Long sectionId) {

        pageAccessService.deleteSection(sectionId);

        return ResponseModels.success(
                "Section deleted successfully."
        );
    }

    /**
     * Update section order
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/section/order")
    public ResponseEntity<?> updateSectionPosition(
            @RequestBody List<UpdatePositionRequest> request) {

        pageAccessService.updateSectionPosition(request);

        return ResponseModels.success(
                "Section positions updated successfully."
        );
    }

    /**
     * Enable / Disable section
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/section/status/{sectionId}")
    public ResponseEntity<?> updateSectionStatus(
            @PathVariable Long sectionId) {

        pageAccessService.updateSectionStatus(sectionId);

        return ResponseModels.success(
                "Section status updated successfully."
        );
    }

}