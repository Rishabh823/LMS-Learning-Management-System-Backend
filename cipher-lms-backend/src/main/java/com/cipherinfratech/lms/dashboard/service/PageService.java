package com.cipherinfratech.lms.dashboard.service;

import com.cipherinfratech.lms.dashboard.dto.*;

import java.util.List;

public interface PageService {

    void addPage(AddPageOnlyRequest request);

    PageResponse getPage(Long pageId);

    void updatePage(Long pageId,
                    UpdatePageRequest request);

    void deletePage(Long pageId);

    void updatePagePosition(
            Long sectionId,
            List<UpdatePositionRequest> request);

    void updatePageStatus(Long pageId);

}