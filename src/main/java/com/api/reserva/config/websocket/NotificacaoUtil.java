package com.api.reserva.config.websocket;

import com.api.reserva.dto.NotificacaoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Utilitário para enviar notificações via WebSocket
 * DESCONTINUADO: Use NotificacaoEventListener em vez disso
 *
 * Este utilitário cria DTOs vazios sem ID, o que não é ideal.
 * O listener automático (NotificacaoEventListener) é mais robusto.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class NotificacaoUtil {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Registrar módulo para suportar LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * @deprecated Use NotificacioEventListener em vez disso
     */
    @Deprecated(since = "2.0", forRemoval = true)
    public static NotificacaoDTO notificarUsuario(Long usuarioId, String titulo, String mensagem) {
        try {
            logger.warn("⚠️ NotificacaoUtil.notificarUsuario() está DESCONTINUADO. Use NotificacaoService.novaNotificacao() ao invés.");

            LocalDateTime agora = LocalDateTime.now();
            NotificacaoDTO notificacaoDTO = new NotificacaoDTO(null, usuarioId, titulo, mensagem, agora, false);
            String json = objectMapper.writeValueAsString(notificacaoDTO);

            System.out.println("📤 Enviando notificação via WebSocket direto para usuário " + usuarioId + ": " + titulo);
            NotificacaoWebSocketHandler.enviarNotificacaoParaUsuario(usuarioId, json);
            logger.info("📢 Notificação enviada para usuário {}: {}", usuarioId, titulo);

            return notificacaoDTO;
        } catch (IOException e) {
            logger.error("❌ Erro ao enviar notificação para usuário {}: {}", usuarioId, e.getMessage());
            return null;
        }
    }

    /**
     * @deprecated Use NotificacioEventListener em vez disso
     */
    @Deprecated(since = "2.0", forRemoval = true)
    public static NotificacaoDTO notificarTodos(String titulo, String mensagem) {
        try {
            logger.warn("⚠️ NotificacaoUtil.notificarTodos() está DESCONTINUADO.");

            LocalDateTime agora = LocalDateTime.now();
            NotificacaoDTO notificacaoDTO = new NotificacaoDTO(null, null, titulo, mensagem, agora, false);
            String json = objectMapper.writeValueAsString(notificacaoDTO);

            System.out.println("📣 Enviando notificação em broadcast via WebSocket: " + titulo);
            NotificacaoWebSocketHandler.enviarNotificacaoParaTodos(json);
            logger.info("📢 Notificação em broadcast enviada: {}", titulo);

            return notificacaoDTO;
        } catch (IOException e) {
            logger.error("❌ Erro ao enviar notificação em broadcast: {}", e.getMessage());
            return null;
        }
    }
}

