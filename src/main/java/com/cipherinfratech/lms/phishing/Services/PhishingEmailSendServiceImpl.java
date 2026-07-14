package com.cipherinfratech.lms.phishing.Services;

import com.cipherinfratech.lms.phishing.entity.PhishingEmailSend;
import com.cipherinfratech.lms.phishing.repositories.PhishingEmailSendRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PhishingEmailSendServiceImpl implements PhishingEmailSendService{
    @Autowired
    private PhishingEmailSendRepo phishingEmailSendRepo;
    @Override
    public List<PhishingEmailSend> getAllPhishingEmailSend() {
        return this.phishingEmailSendRepo.findAll();
    }

    @Override
    public List<PhishingEmailSend> getAllPhishingEmailSendByOrganizationId(long orgId) {
        return this.phishingEmailSendRepo.findAllByOrgId(orgId);
    }

    @Override
    public PhishingEmailSend saveSentEmails(PhishingEmailSend phishingEmailSend) {
        return this.phishingEmailSendRepo.save(phishingEmailSend);
    }

    @Override
    public PhishingEmailSend getPhishingEmailSendByOrganizationIdAndUserId(long orgId, UUID userId) {
        return this.phishingEmailSendRepo.getAllPhishingEmailSendByOrgIdAndUserId(orgId,userId);
    }

    @Override
    public PhishingEmailSend getPhishingEmailSendByOrganizationIdAndUserIdAndTemplate(long orgId, UUID userId, String template) {
        return this.phishingEmailSendRepo.getAllPhishingEmailSendByOrgIdAndUserIdAndTemplate(orgId,userId,template);
    }

    @Override
    public PhishingEmailSend updateSentEmails(PhishingEmailSend phishingEmailSend) {
        return this.phishingEmailSendRepo.save(phishingEmailSend);

    }
}
