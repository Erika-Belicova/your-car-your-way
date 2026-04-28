package com.yourcaryourway.backend.configuration.websocket;

import com.yourcaryourway.backend.security.JwtChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Security configuration for WebSocket connections.
 * Validates JWT tokens on WebSocket CONNECT frames.
 */
@Configuration
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    public WebSocketSecurityConfig(JwtChannelInterceptor jwtChannelInterceptor) {
        this.jwtChannelInterceptor = jwtChannelInterceptor;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // validate JWT tokens on incoming WebSocket messages
        registration.interceptors(jwtChannelInterceptor);
    }

}