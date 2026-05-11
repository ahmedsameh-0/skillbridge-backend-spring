package com.skillbridge.config;

import com.skillbridge.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Only validate JWT for CONNECT command
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeader = accessor.getNativeHeader("Authorization");

            if (authHeader == null || authHeader.isEmpty()) {
                throw new AccessDeniedException("Authorization header is missing");
            }

            String token = authHeader.get(0);
            if (!token.startsWith("Bearer ")) {
                throw new AccessDeniedException("Invalid authorization header format");
            }

            token = token.substring(7); // Remove "Bearer " prefix

            try {
                // Validate JWT token
                JwtDecoder jwtDecoder = createJwtDecoder();
                jwtDecoder.decode(token);

                // Token is valid, set authenticated user for WebSocket session
                Principal principal = () -> "websocket-user";
                accessor.setUser(principal);
            } catch (Exception ex) {
                throw new AccessDeniedException("Invalid JWT token: " + ex.getMessage());
            }
        }

        return message;
    }

    private JwtDecoder createJwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(jwtProperties.getKey().getBytes(), "HmacSHA256");
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
