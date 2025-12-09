package com.api.reserva.config.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private NotificacaoWebSocketHandler notificacaoHandler;

    @Autowired
    private JwtHandshakeInterceptor jwtInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificacaoHandler, "/ws/notificacoes")
                .addInterceptors(jwtInterceptor, new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*")
                .setAllowedOriginPatterns("*");
    }
}

