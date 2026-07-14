package com.cipherinfratech.lms.notification.listeners;

import com.cipherinfratech.lms.notification.dto.NotificationType;
import com.cipherinfratech.lms.notification.entities.Notification;
import com.cipherinfratech.lms.notification.events.OrganizationUserCreatedEvent;
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
public class OrganizationUserNotificationListener {

    private final NotificationService notificationService;
    private final WebSocketService webSocketService;

    // 🔹 1. ADMIN HANDLER
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdminNotification(OrganizationUserCreatedEvent event) {

        for (String adminEmail : event.adminEmails()) {

            Notification notification = notificationService.create(
                    adminEmail,
                    "New User Added",
                    "User '" + event.userName() + "' added to group '" + event.groupName() + "'.",
                    NotificationType.ORGANIZATION_GROUP_USER_CREATED,
                    "/home/userlist"
            );

            webSocketService.sendToUser(adminEmail, notification);

            log.info("🚀 [ADMIN WS SENT] email={}, user={}",
                    adminEmail,
                    event.userName()
            );
        }
    }

    // 🔹 2. ORGANIZATION HANDLER
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrganizationNotification(OrganizationUserCreatedEvent event) {

        Notification notification = notificationService.create(
                event.organizationEmail(),
                "User Added",
                "User '" + event.userName() + "' added to group '" + event.groupName() + "'.",
                NotificationType.ORGANIZATION_GROUP_USER_CREATED,
                "/home/userlist"
        );

        webSocketService.sendToUser(event.organizationEmail(), notification);

        log.info("🚀 [ORG WS SENT] email={}, user={}",
                event.organizationEmail(),
                event.userName()
        );
    }

    // 🔹 3. USER HANDLER
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserNotification(OrganizationUserCreatedEvent event) {

        Notification notification = notificationService.create(
                event.userEmail(),
                "Welcome",
                "You have been added to group '" + event.groupName() + "'. Start your learning course now",
                NotificationType.ORGANIZATION_GROUP_USER_CREATED,
                "/home/courses"
        );

        webSocketService.sendToUser(event.userEmail(), notification);

        log.info("🚀 [USER WS SENT] email={}", event.userEmail());
    }
}