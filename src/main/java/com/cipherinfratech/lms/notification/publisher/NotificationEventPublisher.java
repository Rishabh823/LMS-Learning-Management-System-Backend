package com.cipherinfratech.lms.notification.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(Object event) {
        publisher.publishEvent(event);
    }
}
