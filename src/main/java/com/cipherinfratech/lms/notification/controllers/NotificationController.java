package com.cipherinfratech.lms.notification.controllers;

import com.cipherinfratech.lms.notification.dto.NotificationProjection;
import com.cipherinfratech.lms.notification.service.NotificationService;
import com.cipherinfratech.lms.utils.ResponseModels;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("all")
    public ResponseEntity<Object> getMyNotifications(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {

            String email = principal.getName(); // 🔥 direct

            Page<NotificationProjection> notifications =
                    service.getUserNotifications(email, page, size);

            return ResponseModels.successWithPayloadPaginated(
                    "Notifications fetched successfully",
                    notifications
            );

        } catch (Exception e) {
            return ResponseModels.exceptionError(e);
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Object> markRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseModels.success("Successfully marked notification as read");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Object> unreadCount(Principal principal) {
        Long count = service.getUnreadCount(principal.getName());
        return ResponseModels.successWithPayload("Fetched unread notification count",
                Map.of(
                        "unreadCount", count
                )
                );
    }

    @PutMapping("all/read")
    public ResponseEntity<?> markAllNotificationsAsReadForUser(Principal principal) {
        int count = service.markAllNotificationsAsReadForUser(principal.getName());
        return ResponseModels.success("Successfully marked " + count + " notifications as read");
    }
}
