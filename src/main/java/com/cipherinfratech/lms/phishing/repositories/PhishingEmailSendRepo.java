package com.cipherinfratech.lms.phishing.repositories;

import com.cipherinfratech.lms.phishing.entity.PhishingEmailSend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhishingEmailSendRepo extends JpaRepository<PhishingEmailSend, Long> {

    List<PhishingEmailSend> findAllByOrgId(long orgId);

    PhishingEmailSend getAllPhishingEmailSendByOrgIdAndUserId(long orgId, UUID userId);

    PhishingEmailSend getAllPhishingEmailSendByOrgIdAndUserIdAndTemplate(long orgId, UUID userId, String template);
}
