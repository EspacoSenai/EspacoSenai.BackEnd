package com.api.reserva.listener;

import com.api.reserva.config.websocket.NotificacaoWebSocketHandler;
import com.api.reserva.dto.NotificacaoDTO;
import com.api.reserva.event.NotificacaoEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoEventListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoEventListener.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Listener que dispara automaticamente quando uma notificação é criada
     * Envia a notificação em tempo real via WebSocket
     */
    @EventListener
    @Async
    public void onNotificacao(NotificacaoEvent event) {
        System.out.println("🔔 Listener disparado - Notificação criada");

        try {
            NotificacaoDTO dto = new NotificacaoDTO(event.getNotificacao());
            Long usuarioId = event.getUsuarioDestinatarioId();

            System.out.println("📢 Enviando notificação via WebSocket - Usuário: " + usuarioId + ", Título: " + dto.getTitulo());

            // Serializar DTO com ObjectMapper para garantir serialização correta
            String json = objectMapper.writeValueAsString(dto);

            // Enviar via WebSocket em tempo real
            NotificacaoWebSocketHandler.enviarNotificacaoParaUsuario(usuarioId, json);

            logger.info("✓ Notificação {} entregue via WebSocket para usuário {}", dto.getId(), usuarioId);
        } catch (Exception e) {
            logger.error("❌ Erro ao enviar notificação via WebSocket: {}", e.getMessage(), e);
            System.err.println("❌ Erro ao enviar notificação: " + e.getMessage());
        }
    }
}

