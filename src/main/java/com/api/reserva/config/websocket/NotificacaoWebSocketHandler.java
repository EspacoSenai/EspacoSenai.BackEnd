package com.api.reserva.config.websocket;

import com.api.reserva.dto.NotificacaoDTO;
import com.api.reserva.repository.NotificacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificacaoWebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoWebSocketHandler.class);
    private static final Map<Long, WebSocketSession> usuarioSessions = new ConcurrentHashMap<>();
    private static NotificacaoRepository notificacaoRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Registrar módulo para suportar LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    public void setNotificacaoRepository(NotificacaoRepository repo) {
        NotificacaoWebSocketHandler.notificacaoRepository = repo;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
            System.out.println("🔗 WebSocket conectado - Verificando usuarioId...");
            System.out.println("📍 Atributos da sessão: " + session.getAttributes());

            // Extrair usuarioId do JWT via atributo ou query string
            Long usuarioId = null;

            // Tentar obter do atributo (setado pelo interceptor)
            Object usuarioIdAttr = session.getAttributes().get("usuarioId");
            if (usuarioIdAttr != null) {
                usuarioId = (Long) usuarioIdAttr;
                System.out.println("✅ usuarioId extraído do atributo: " + usuarioId);
            } else {
                System.out.println("⚠️ usuarioId não está no atributo - tentando fallback da URL");
                // Fallback: extrair da URL
                String usuarioIdParam = extractUsuarioIdFromUri(session.getUri() != null ? session.getUri().toString() : "");
                if (usuarioIdParam != null && !usuarioIdParam.isEmpty()) {
                    usuarioId = Long.parseLong(usuarioIdParam);
                    System.out.println("✅ usuarioId extraído da URL: " + usuarioId);
                }
            }

            if (usuarioId != null) {
                // Remover sessão anterior se existir
                WebSocketSession sessionAnterior = usuarioSessions.get(usuarioId);
                if (sessionAnterior != null && sessionAnterior.isOpen()) {
                    try {
                        sessionAnterior.close();
                        System.out.println("⚠️ Sessão anterior do usuário " + usuarioId + " foi fechada");
                    } catch (IOException e) {
                        logger.warn("⚠️ Erro ao fechar sessão anterior: {}", e.getMessage());
                    }
                }

                usuarioSessions.put(usuarioId, session);
                System.out.println("✅ Usuário " + usuarioId + " conectado ao WebSocket. Total de conexões: " + usuarioSessions.size());

                // Enviar notificações não lidas ao conectar
                enviarNotificacoesNaoLidas(usuarioId, session);
            } else {
                System.err.println("❌ Não foi possível extrair usuarioId");
                session.close();
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar WebSocket: " + e.getMessage());
            logger.error("❌ Erro ao conectar WebSocket: ", e);
            try {
                session.close();
            } catch (IOException ex) {
                logger.error("❌ Erro ao fechar sessão com erro: {}", ex.getMessage());
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        // Encontrar e remover a sessão do usuário
        Long usuarioIdEncontrado = null;
        for (Map.Entry<Long, WebSocketSession> entry : usuarioSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                usuarioIdEncontrado = entry.getKey();
                break;
            }
        }

        if (usuarioIdEncontrado != null) {
            usuarioSessions.remove(usuarioIdEncontrado);
            System.out.println("✓ Usuário " + usuarioIdEncontrado + " desconectado. Total de conexões: " + usuarioSessions.size());
        } else {
            System.out.println("✓ Sessão WebSocket desconectada");
        }
    }

    private void enviarNotificacoesNaoLidas(Long usuarioId, WebSocketSession session) {
        try {
            if (notificacaoRepository == null) {
                System.err.println("✗ NotificacaoRepository não foi injetado");
                return;
            }

            var notificacoes = notificacaoRepository.findNaoLidasByUsuarioId(usuarioId);

            if (!notificacoes.isEmpty()) {
                for (var notif : notificacoes) {
                    NotificacaoDTO dto = new NotificacaoDTO(notif);
                    String json = objectMapper.writeValueAsString(dto);
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(json));
                    }
                }
                System.out.println("✓ " + notificacoes.size() + " notificações não lidas enviadas para usuário: " + usuarioId);
            }
        } catch (IOException e) {
            logger.error("✗ Erro ao enviar notificações não lidas: {}", e.getMessage());
        }
    }

    public static void enviarNotificacaoParaUsuario(Long usuarioId, String mensagem) throws IOException {
        WebSocketSession session = usuarioSessions.get(usuarioId);

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(mensagem));
                System.out.println("✓ Notificação enviada em tempo real para usuário: " + usuarioId);
            } catch (IOException e) {
                System.err.println("❌ Erro ao enviar mensagem para usuário " + usuarioId + ": " + e.getMessage());
                logger.error("❌ Erro ao enviar mensagem para usuário {}: {}", usuarioId, e.getMessage());
                // Remover sessão com erro
                usuarioSessions.remove(usuarioId);
                throw e;
            }
        } else {
            System.out.println("⚠️ Usuário " + usuarioId + " não está conectado ao WebSocket (será entregue ao conectar)");
        }
    }

    public static void enviarNotificacaoParaTodos(String mensagem) throws IOException {
        int enviadas = 0;
        for (Map.Entry<Long, WebSocketSession> entry : usuarioSessions.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(mensagem));
                    enviadas++;
                } catch (IOException e) {
                    logger.error("❌ Erro ao enviar broadcast para usuário {}: {}", entry.getKey(), e.getMessage());
                    usuarioSessions.remove(entry.getKey());
                }
            }
        }
        System.out.println("✓ Notificação enviada em broadcast para " + enviadas + " usuários");
    }

    private static String extractUsuarioIdFromUri(String uri) {
        if (uri.contains("usuarioId=")) {
            String[] parts = uri.split("usuarioId=");
            if (parts.length > 1) {
                return parts[1].split("&")[0];
            }
        }
        return null;
    }
}

