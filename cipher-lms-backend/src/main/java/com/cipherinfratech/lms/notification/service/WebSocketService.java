package com.cipherinfratech.lms.notification.service;

import com.cipherinfratech.lms.notification.entities.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(String emailId, Notification notification) {

        String destination = "/queue/notifications";

        log.info("📡 [WS PUSH START] userId={}, destination={}", emailId, destination);

        messagingTemplate.convertAndSendToUser(
                emailId,
                "/queue/notifications",
                notification
        );

        log.info("📡 [WS PUSH SUCCESS] userId={}, notificationId={}",
                emailId,
                notification.getId()
        );
    }
}