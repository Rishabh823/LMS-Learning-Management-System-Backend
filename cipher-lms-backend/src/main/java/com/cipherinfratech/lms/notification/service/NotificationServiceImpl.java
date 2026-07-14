package com.cipherinfratech.lms.notification.service;

import com.cipherinfratech.lms.notification.dto.NotificationProjection;
import com.cipherinfratech.lms.notification.dto.NotificationType;
import com.cipherinfratech.lms.notification.entities.Notification;
import com.cipherinfratech.lms.notification.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    @Override
    public Notification create(
            String email,
            String title,
            String message,
            NotificationType type,
            String redirectUrl
    ) {
        Notification notification = new Notification();
        notification.setEmail(email); // 🔥 changed
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRedirectUrl(redirectUrl);

        return repository.save(notification);
    }

    @Override
    public Page<NotificationProjection> getUserNotifications(String email, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return repository.findByEmailOrderByCreatedDateDesc(email, pageable);
    }

    @Override
    public void markAsRead(Long id) {
        Notification n = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        repository.save(n);
    }

    @Override
    public long getUnreadCount(String email) {
        return repository.countByEmailAndIsReadFalse(email);
    }

    @Override
    public int markAllNotificationsAsReadForUser(String email) {
        return repository.markAllAsReadByEmail(email);
    }
}
