package com.cipherinfratech.lms.notification.entities;

import com.cipherinfratech.lms.notification.dto.NotificationType;
import com.cipherinfratech.lms.utils.Tracker;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Notification extends Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // 🔥 changed

    private String title;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String redirectUrl;

    private boolean isRead = false;
}
