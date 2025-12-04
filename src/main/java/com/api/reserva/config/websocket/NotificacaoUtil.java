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
 * Simplifica o envio de mensagens para usuários específicos ou broadcast
 */
public class NotificacaoUtil {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Registrar módulo para suportar LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Envia notificação para um usuário específico via WebSocket
     *
     * @param usuarioId ID do usuário
     * @param titulo Título da notificação
     * @param mensagem Mensagem da notificação
     * @param tipo Tipo/categoria da notificação (INFO, ALERTA, SUCESSO, ERRO)
     * @return NotificacaoDTO com os dados da notificação enviada
     */
    public static NotificacaoDTO notificarUsuario(Long usuarioId, String titulo, String mensagem, String tipo) {
        try {
            LocalDateTime agora = LocalDateTime.now();
            NotificacaoDTO notificacaoDTO = new NotificacaoDTO(null, usuarioId, titulo, mensagem, agora, false);
            String json = objectMapper.writeValueAsString(notificacaoDTO);
            NotificacaoWebSocketHandler.enviarNotificacaoParaUsuario(usuarioId, json);
            logger.info("📢 Notificação enviada para usuário {}: {}", usuarioId, titulo);

            return notificacaoDTO;
        } catch (IOException e) {
            logger.error("❌ Erro ao enviar notificação para usuário {}: {}", usuarioId, e.getMessage());
            return null;
        }
    }

    /**
     * Envia notificação em broadcast para todos os usuários conectados via WebSocket
     *
     * @param titulo Título da notificação
     * @param mensagem Mensagem da notificação
     * @param tipo Tipo/categoria da notificação (INFO, ALERTA, SUCESSO, ERRO)
     * @return NotificacaoDTO com os dados da notificação enviada
     */
    public static NotificacaoDTO notificarTodos(String titulo, String mensagem, String tipo) {
        try {
            LocalDateTime agora = LocalDateTime.now();
            NotificacaoDTO notificacaoDTO = new NotificacaoDTO(null, null, titulo, mensagem, agora, false);
            String json = objectMapper.writeValueAsString(notificacaoDTO);
            NotificacaoWebSocketHandler.enviarNotificacaoParaTodos(json);
            logger.info("📢 Notificação em broadcast enviada: {}", titulo);

            return notificacaoDTO;
        } catch (IOException e) {
            logger.error("❌ Erro ao enviar notificação em broadcast: {}", e.getMessage());
            return null;
        }
    }
}

