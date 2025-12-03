package com.api.reserva.listener;

import com.api.reserva.config.websocket.NotificacaoUtil;
import com.api.reserva.dto.NotificacaoDTO;
import com.api.reserva.event.NotificacaoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoEventListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoEventListener.class);

    /**
     * Listener que dispara automaticamente quando uma notificação é criada
     * Envia a notificação em tempo real via WebSocket
     */
    @EventListener
    public void onNotificacao(NotificacaoEvent event) {
        System.out.println("🔔 Listener disparado - Notificação criada");

        NotificacaoDTO dto = new NotificacaoDTO(event.getNotificacao());
        Long usuarioId = event.getUsuarioDestinatarioId();
        dto.setUsuarioId(usuarioId);

        System.out.println("📢 Enviando notificação via WebSocket - Usuário: " + usuarioId + ", Título: " + dto.getTitulo());

        // Enviar via WebSocket em tempo real
        NotificacaoUtil.notificarUsuario(
                usuarioId,
                dto.getTitulo(),
                dto.getMensagem(),
                "INFO"
        );

        logger.info("Notificação {} entregue via WebSocket para usuário {}", dto.getId(), usuarioId);
    }
}

