package com.yourcaryourway.backend.security;

import com.yourcaryourway.backend.exception.JwtValidationException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

/**
 * Intercepts incoming WebSocket messages before they are processed.
 * Validates the JWT token on CONNECT frames to authenticate the user.
 */
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public JwtChannelInterceptor(JwtDecoder jwtDecoder,
                                 JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class);

        // authenticate the user when they first connect to the WebSocket
        if (isConnectFrame(accessor)) {
            String token = extractToken(accessor);
            authenticateSession(accessor, token);
        }

        return message;
    }

    // check if the incoming frame is a CONNECT frame
    private boolean isConnectFrame(StompHeaderAccessor accessor) {
        return accessor != null && StompCommand.CONNECT.equals(accessor.getCommand());
    }

    // extract and validate the Bearer token from the Authorization header
    private String extractToken(StompHeaderAccessor accessor) {
        final String bearerPrefix = "Bearer ";
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(bearerPrefix)) {
            throw new JwtValidationException("Missing or invalid Authorization header");
        }
        return authHeader.substring(bearerPrefix.length());
    }

    // decode JWT and set authenticated user on the WebSocket session
    private void authenticateSession(StompHeaderAccessor accessor, String token) {
        try {
            var jwt = jwtDecoder.decode(token);
            Authentication authentication = jwtAuthenticationConverter.convert(jwt);
            accessor.setUser(authentication);
        } catch (Exception e) {
            throw new JwtValidationException("The JWT token is invalid");
        }
    }

}