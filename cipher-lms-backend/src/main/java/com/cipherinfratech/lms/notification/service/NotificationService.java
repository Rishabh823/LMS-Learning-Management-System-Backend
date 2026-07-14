package com.cipherinfratech.lms.notification.service;

import com.cipherinfratech.lms.notification.dto.NotificationProjection;
import com.cipherinfratech.lms.notification.dto.NotificationType;
import com.cipherinfratech.lms.notification.entities.Notification;
import org.springframework.data.domain.Page;


public interface NotificationService {

    Notification create(
            String email,   // 🔥 changed
            String title,
            String message,
            NotificationType type,
            String redirectUrl
    );

    Page<NotificationProjection> getUserNotifications(String email, int page, int size);

    void markAsRead(Long id);

    long getUnreadCount(String email);

    int markAllNotificationsAsReadForUser(String email);
}