package com.cipherinfratech.lms.notification.listeners;

import com.cipherinfratech.lms.notification.dto.NotificationType;
import com.cipherinfratech.lms.notification.entities.Notification;
import com.cipherinfratech.lms.notification.events.OrganizationCreatedEvent;
import com.cipherinfratech.lms.notification.service.NotificationService;
import com.cipherinfratech.lms.notification.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrganizationNotificationListener {

    private final NotificationService notificationService;
    private final WebSocketService webSocketService;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdminNotification(OrganizationCreatedEvent event) {

        log.info("🔔 [ADMIN EVENT] orgId={}, orgName={}, totalAdmins={}",
                event.organizationId(),
                event.organizationName(),
                event.adminEmails().size()
        );

        for (String adminEmail : event.adminEmails()) {

            Notification notification = notificationService.create(
                    adminEmail,
                    "Organization Created",
                    "A new organization '" + event.organizationName() + "' has been created.",
                    NotificationType.ORGANIZATION_CREATED,
                    "/home/allorganisation"
            );

            webSocketService.sendToUser(adminEmail, notification);

            log.info("🚀 [ADMIN WS SENT] email={}, notificationId={}",
                    adminEmail,
                    notification.getId()
            );
        }
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrganizationNotification(OrganizationCreatedEvent event) {

        log.info("🔔 [ORG EVENT] orgId={}, orgName={}, orgEmail={}",
                event.organizationId(),
                event.organizationName(),
                event.organizationEmail()
        );

        String orgEmail = event.organizationEmail();

        Notification notification = notificationService.create(
                orgEmail,
                "Welcome to the Platform",
                "Your organization '" + event.organizationName() + "' has been successfully registered.",
                NotificationType.ORGANIZATION_CREATED,
                "/home" // same redirect as you asked
        );

        webSocketService.sendToUser(orgEmail, notification);

        log.info("🚀 [ORG WS SENT] email={}, notificationId={}",
                orgEmail,
                notification.getId()
        );
    }

}