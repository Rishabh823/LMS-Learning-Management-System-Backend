package com.cipherinfratech.lms.dashboard.service;

import com.cipherinfratech.lms.dashboard.dto.SidebarDTO;

import java.security.Principal;

public interface SidebarService {

    SidebarDTO getSidebar(Principal principal);

}
