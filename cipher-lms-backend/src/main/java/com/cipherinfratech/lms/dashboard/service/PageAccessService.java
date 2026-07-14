package com.cipherinfratech.lms.dashboard.service;

import com.cipherinfratech.lms.dashboard.dto.*;

import java.util.List;

public interface PageAccessService {
    void addSection(AddSectionRequest request);

    void updateSection(Long sectionId,
                       UpdateSectionRequest request);

    void deleteSection(Long sectionId);


    void updateSectionPosition(List<UpdatePositionRequest> request);

    List<SectionResponse> getAllSections();

    SectionResponse getSection(Long sectionId);

    void updateSectionStatus(Long sectionId);

}
