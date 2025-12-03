package com.api.reserva.config.websocket;

import com.api.reserva.repository.NotificacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificacaoWebSocketHandler extends AbstractWebSocketHandler {

    private static final Map<Long, WebSocketSession> usuarioSessions = new ConcurrentHashMap<>();
    private static NotificacaoRepository notificacaoRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public void setNotificacaoRepository(NotificacaoRepository repo) {
        NotificacaoWebSocketHandler.notificacaoRepository = repo;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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
                usuarioSessions.put(usuarioId, session);
                System.out.println("✅ Usuário " + usuarioId + " conectado ao WebSocket");

                // Enviar notificações não lidas ao conectar
                enviarNotificacoesNaoLidas(usuarioId, session);
            } else {
                System.err.println("❌ Não foi possível extrair usuarioId");
                session.close();
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar WebSocket: " + e.getMessage());
            e.printStackTrace();
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        usuarioSessions.values().remove(session);
        System.out.println("✓ Sessão WebSocket desconectada");
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
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("id", notif.getId());
                    payload.put("titulo", notif.getTitulo());
                    payload.put("mensagem", notif.getMensagem());
                    payload.put("criadoEm", notif.getCriadoEm());
                    payload.put("lida", notif.isLida());

                    String json = objectMapper.writeValueAsString(payload);
                    session.sendMessage(new TextMessage(json));
                }
                System.out.println("✓ " + notificacoes.size() + " notificações não lidas enviadas para usuário: " + usuarioId);
            }
        } catch (IOException e) {
            System.err.println("✗ Erro ao enviar notificações não lidas: " + e.getMessage());
        }
    }

    public static void enviarNotificacaoParaUsuario(Long usuarioId, String mensagem) throws IOException {
        WebSocketSession session = usuarioSessions.get(usuarioId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(mensagem));
            System.out.println("✓ Notificação enviada para usuário: " + usuarioId);
        } else {
            System.out.println("⚠️ Usuário " + usuarioId + " não está conectado ao WebSocket");
        }
    }

    public static void enviarNotificacaoParaTodos(String mensagem) throws IOException {
        for (WebSocketSession session : usuarioSessions.values()) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(mensagem));
            }
        }
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

