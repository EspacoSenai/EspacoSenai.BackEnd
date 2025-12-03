package com.api.reserva.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário para enviar notificações via WebSocket
 * Simplifica o envio de mensagens para usuários específicos ou broadcast
 */
public class NotificacaoUtil {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Envia notificação para um usuário específico via WebSocket
     *
     * @param usuarioId ID do usuário
     * @param titulo Título da notificação
     * @param mensagem Mensagem da notificação
     * @param tipo Tipo/categoria da notificação (INFO, ALERTA, SUCESSO, ERRO)
     */
    public static void notificarUsuario(Long usuarioId, String titulo, String mensagem, String tipo) {
        try {
            Map<String, Object> payload = criarPayload(titulo, mensagem, tipo);
            String json = objectMapper.writeValueAsString(payload);
            NotificacaoWebSocketHandler.enviarNotificacaoParaUsuario(usuarioId, json);
            logger.info("Notificação enviada para usuário {}: {}", usuarioId, titulo);
        } catch (IOException e) {
            logger.error("Erro ao enviar notificação para usuário {}: {}", usuarioId, e.getMessage());
        }
    }

    /**
     * Envia notificação em broadcast para todos os usuários conectados via WebSocket
     *
     * @param titulo Título da notificação
     * @param mensagem Mensagem da notificação
     * @param tipo Tipo/categoria da notificação (INFO, ALERTA, SUCESSO, ERRO)
     */
    public static void notificarTodos(String titulo, String mensagem, String tipo) {
        try {
            Map<String, Object> payload = criarPayload(titulo, mensagem, tipo);
            String json = objectMapper.writeValueAsString(payload);
            NotificacaoWebSocketHandler.enviarNotificacaoParaTodos(json);
            logger.info("Notificação em broadcast enviada: {}", titulo);
        } catch (IOException e) {
            logger.error("Erro ao enviar notificação em broadcast: {}", e.getMessage());
        }
    }

    /**
     * Cria um payload padronizado para a notificação
     *
     * @param titulo Título da notificação
     * @param mensagem Mensagem da notificação
     * @param tipo Tipo/categoria (INFO, ALERTA, SUCESSO, ERRO)
     * @return Mapa com os dados da notificação
     */
    private static Map<String, Object> criarPayload(String titulo, String mensagem, String tipo) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("titulo", titulo);
        payload.put("mensagem", mensagem);
        payload.put("tipo", tipo);
        payload.put("timestamp", LocalDateTime.now().toString()); // Converter para String
        return payload;
    }
}

