package com.cipherinfratech.lms.phishing.controllers;

import com.cipherinfratech.lms.phishing.Services.PhishingEmailSendService;
import com.cipherinfratech.lms.phishing.entity.PhishingEmailSend;
import com.cipherinfratech.lms.users.services.UserService;
import com.cipherinfratech.lms.utils.ResponseModels;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/phishing")
@AllArgsConstructor
public class PhishingController {

    private PhishingEmailSendService phishingEmailSendService;

    private UserService userService;

    @GetMapping("/{orgId}")
    ResponseEntity<Object> getAllPhishingSetEmail(@PathVariable long orgId, Principal principal) {
        try {
            if (userService.isAdmin(principal.getName())) {
                List<PhishingEmailSend> phishingEmailSendsList = this.phishingEmailSendService.getAllPhishingEmailSend();
                return ResponseModels.successWithPayload("All Phishing sent email", phishingEmailSendsList);
            }
            List<PhishingEmailSend> phishingEmailSendsList = this.phishingEmailSendService.getAllPhishingEmailSendByOrganizationId(orgId);
            return ResponseModels.successWithPayload("All Phishing sent email", phishingEmailSendsList);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return ResponseModels.unknownError();
        }
    }
 @PutMapping("/click")
    ResponseEntity<Object> updatePhishingSetEmailOnEmailClick(@RequestBody PhishingEmailSend phishingEmailSend) {
        try {
         PhishingEmailSend emailSendData = this.phishingEmailSendService.getPhishingEmailSendByOrganizationIdAndUserIdAndTemplate(phishingEmailSend.getOrgId(),phishingEmailSend.getUserId(),phishingEmailSend.getTemplate());

         emailSendData.setEmailOpen(true);
         emailSendData.setCountry_code(phishingEmailSend.getCountry_code());
         emailSendData.setCountry_name(phishingEmailSend.getCountry_name());
         emailSendData.setCity(phishingEmailSend.getCity());
         emailSendData.setPostal(phishingEmailSend.getPostal());
         emailSendData.setLatitude(phishingEmailSend.getLatitude());
         emailSendData.setLongitude(phishingEmailSend.getLongitude());
         emailSendData.setIPv4(phishingEmailSend.getIPv4());
         emailSendData.setState(phishingEmailSend.getState());
         this.phishingEmailSendService.updateSentEmails(emailSendData);
         return ResponseModels.requestedAccepted();

        } catch (Exception e) {
            //throw new RuntimeException(e);
            return ResponseModels.unknownError();
        }
    }
@PutMapping("/getData")
    ResponseEntity<Object> updatePhishingSetEmailCredentials(@RequestBody PhishingEmailSend phishingEmailSend) {
        try {
         PhishingEmailSend emailSendData = this.phishingEmailSendService.getPhishingEmailSendByOrganizationIdAndUserIdAndTemplate(phishingEmailSend.getOrgId(),phishingEmailSend.getUserId(),phishingEmailSend.getTemplate());

         emailSendData.setEmailOpen(true);
         emailSendData.setCredentialsUserName(phishingEmailSend.getCredentialsUserName());
         emailSendData.setCredentialsPassword(phishingEmailSend.getCredentialsPassword());
         this.phishingEmailSendService.updateSentEmails(emailSendData);
         return ResponseModels.requestedAccepted();

        } catch (Exception e) {
            //throw new RuntimeException(e);
            return ResponseModels.unknownError();
        }
    }

}
