package com.cipherinfratech.lms.phishing.Services;

import com.cipherinfratech.lms.phishing.entity.PhishingEmailSend;

import java.util.List;
import java.util.UUID;

public interface PhishingEmailSendService {

    List<PhishingEmailSend> getAllPhishingEmailSend();
    List<PhishingEmailSend> getAllPhishingEmailSendByOrganizationId(long orgId);

    PhishingEmailSend saveSentEmails(PhishingEmailSend phishingEmailSend);
    PhishingEmailSend updateSentEmails(PhishingEmailSend phishingEmailSend);

    PhishingEmailSend getPhishingEmailSendByOrganizationIdAndUserId(long orgId, UUID userId);

    PhishingEmailSend getPhishingEmailSendByOrganizationIdAndUserIdAndTemplate(long orgId, UUID userId, String template);
}
