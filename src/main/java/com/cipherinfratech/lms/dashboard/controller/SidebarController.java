package com.cipherinfratech.lms.dashboard.controller;

import com.cipherinfratech.lms.dashboard.dto.SidebarDTO;
import com.cipherinfratech.lms.dashboard.service.SidebarService;
import com.cipherinfratech.lms.utils.ResponseModels;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("sidebar")
public class SidebarController {

    private final SidebarService service;

    @GetMapping
    public ResponseEntity<?> getSidebar(Principal principal) {
        SidebarDTO response = service.getSidebar(principal);
        return ResponseModels.successWithPayload("Fetched sidebar", response);
    }

}
