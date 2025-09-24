package com.example.kidic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple broker for real-time messaging
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app"); // client → server
        config.setUserDestinationPrefix("/family"); // per-family notifications
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint where clients connect (SockJS fallback enabled)
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}
