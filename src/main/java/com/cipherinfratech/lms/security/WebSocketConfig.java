package com.cipherinfratech.lms.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.config.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    // 🔥 THIS IS THE MAIN PART
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null) return message;

                // 🔥 ONLY HANDLE CONNECT
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    List<String> authHeaders = accessor.getNativeHeader("Authorization");

                    if (authHeaders == null || authHeaders.isEmpty()) {
                        log.warn("❌ WS CONNECT rejected: Missing Authorization header");
                        return null;
                    }

                    String token = authHeaders.get(0);

                    if (!token.startsWith("Bearer ")) {
                        log.warn("❌ WS CONNECT rejected: Invalid header format");
                        return null;
                    }

                    token = token.substring(7);

                    try {
                        String username = jwtService.extractUserName(token);
                        String role = jwtService.extractRole(token);

                        if (username == null) {
                            log.warn("❌ WS CONNECT rejected: invalid token");
                            return null;
                        }

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        List.of(() -> "ROLE_" + role)
                                );

                        accessor.setUser(auth);

                        log.info("✅ WS AUTH SUCCESS: {}", username);

                    } catch (Exception e) {
                        log.error("❌ WS AUTH FAILED: {}", e.getMessage());
                        return null;
                    }
                }

                return message;
            }
        });
    }
}