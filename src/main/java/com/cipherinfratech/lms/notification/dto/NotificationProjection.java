package com.cipherinfratech.lms.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public interface NotificationProjection {

    Long getId();
    String getEmail();
    String getTitle();
    String getMessage();
    NotificationType getType();
    String getRedirectUrl();
    boolean getIsRead();

    @JsonFormat(pattern = "dd MMM yyyy hh:mm:ss a")
    LocalDateTime getCreatedDate();
}