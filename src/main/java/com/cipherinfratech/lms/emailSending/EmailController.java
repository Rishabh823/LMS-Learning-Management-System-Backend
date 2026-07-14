package com.cipherinfratech.lms.emailSending;


import com.cipherinfratech.lms.phishing.Services.PhishingEmailSendService;
import com.cipherinfratech.lms.phishing.entity.PhishingEmailSend;
import com.cipherinfratech.lms.utils.ResponseModels;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

@RestController
@AllArgsConstructor
public class EmailController {

    private final EmailService emailService;

    private PhishingEmailSendService phishingEmailSendService;



    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestBody PhishingEmailSend emailRequest) {
        try {
            // emailService.sendEmail(emailRequest.getEmailId(), emailRequest.getSubject(), emailRequest.getBody());
        this.phishingEmailSendService.saveSentEmails(emailRequest);
            //SendEmailServices.sendMail(emailRequest.getBody(),emailRequest.getSubject(), emailRequest.getTo());
            return ResponseModels.success("Email sent successfully!");
        } catch (Exception e) {
            return ResponseModels.exceptionError(e);
        }
    }
    @PostMapping("/send-html-email")
    public ResponseEntity<Object> sendHtmlEmail(@RequestBody EmailRequestModel emailRequest) {
        Context context = new Context();
        context.setVariable("message", emailRequest.getBody());

        emailService.sendEmailWithHtmlTemplate(emailRequest.getTo(), emailRequest.getSubject(), "email-template", context);
        //return "HTML email sent successfully!";
        return ResponseModels.success("HTML email sent successfully!");
    }

    @PostMapping("/send-verifyCertificateLink-email")
    public ResponseEntity<Object> verifyCertificateLinkEmail(@RequestBody EmailRequestModel emailRequest) {
        Context context = new Context();
        context.setVariable("message", "");

        String subject = "Certificates Link";
        emailService.sendEmailWithHtmlTemplate("mayank@cybersigmacs.com",subject, "certificate-link-template", context);
        //return "HTML email sent successfully!";
        return ResponseModels.success("Email sent successfully!");
    }
}

