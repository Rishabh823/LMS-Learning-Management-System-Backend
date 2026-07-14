package com.cipherinfratech.lms.notification.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        log.info("🟢 WS CONNECTED: {}", event.getMessage());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        log.info("🔴 WS DISCONNECTED: sessionId={}", event.getSessionId());
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        log.info("📩 WS SUBSCRIBED: {}", event.getMessage());
    }
}
