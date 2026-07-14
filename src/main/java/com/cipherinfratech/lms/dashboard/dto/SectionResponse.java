package com.cipherinfratech.lms.dashboard.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SectionResponse {

    private Long id;

    private String name;

    private Integer position;

    private Boolean status;

    private List<PageResponse> pages = new ArrayList<>();

}