package com.api.reserva.controller;

import com.api.reserva.dto.NotificacaoDTO;
import com.api.reserva.service.NotificacaoService;
import com.api.reserva.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller para gerenciar notificações do usuário
 */
@RestController
@RequestMapping("notificacao")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * GET /notificacao/minhas
     * Listar notificações do usuário autenticado
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/minhas")
    public ResponseEntity<List<NotificacaoDTO>> minhasNotificacoes(Authentication authentication) {
        List<NotificacaoDTO> notificacoes = notificacaoService.buscarMinhas(authentication);
        return ResponseEntity.ok(notificacoes);
    }

    /**
     * PUT /notificacao/ler/{id}
     * Marcar notificação como lida
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @PatchMapping("/ler/{id}")
    public ResponseEntity<Object> marcarComoLida(Authentication authentication, @PathVariable Long id) {
        notificacaoService.lerNotificacao(authentication, id);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Notificação marcada como lida.");
    }

    /**
     * DELETE /notificacao/{id}
     * Deletar notificação
     * Valida se o usuário autenticado é o dono da notificação
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id, Authentication authentication) {
        notificacaoService.deletarNotificacaoSeguro(authentication, id);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Notificação deletada com sucesso.");
    }

    /**
     * POST /notificacao/broadcast
     * Enviar notificação em broadcast (ADMIN ONLY)
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @PostMapping("/broadcast")
    public ResponseEntity<Object> enviarBroadcast(@RequestBody NotificacaoDTO dto) {
        notificacaoService.enviarBroadcast(dto);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Broadcast enviado para todos os usuários.");
    }

    /**
     * POST /notificacao/ws/usuario/{usuarioId}
     * Enviar notificação via WebSocket para um usuário específico (ADMIN/COORDENADOR)
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @PostMapping("/ws/usuario/{usuarioId}")
    public ResponseEntity<Object> enviarNotificacaoUsuario(
            @PathVariable Long usuarioId,
            @RequestBody Map<String, String> payload) {
        try {
            String titulo = payload.getOrDefault("titulo", "Notificação");
            String mensagem = payload.getOrDefault("mensagem", "");

            System.out.println("📨 Enviando notificação WebSocket para usuário " + usuarioId + ": " + titulo);
            notificacaoService.enviarNotificacaoWebSocket(usuarioId, titulo, mensagem);
            return ResponseBuilder.respostaSimples(HttpStatus.OK, "Notificação enviada via WebSocket com sucesso.");
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar notificação: " + e.getMessage());
            return ResponseBuilder.respostaSimples(HttpStatus.BAD_REQUEST, "Erro ao enviar notificação: " + e.getMessage());
        }
    }

    /**
     * POST /notificacao/ws/todos
     * Enviar notificação via WebSocket para todos os usuários conectados (ADMIN ONLY)
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @PostMapping("/ws/todos")
    public ResponseEntity<Object> enviarNotificacaoTodos(@RequestBody Map<String, String> payload) {
        try {
            String titulo = payload.getOrDefault("titulo", "Notificação do Sistema");
            String mensagem = payload.getOrDefault("mensagem", "");

            NotificacaoDTO dto = new NotificacaoDTO(null, null, titulo, mensagem, LocalDateTime.now(), false);
            System.out.println("📣 Enviando notificação broadcast: " + titulo);
            notificacaoService.enviarBroadcast(dto);
            return ResponseBuilder.respostaSimples(HttpStatus.OK, "Notificação enviada para todos via WebSocket com sucesso.");
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar broadcast: " + e.getMessage());
            return ResponseBuilder.respostaSimples(HttpStatus.BAD_REQUEST, "Erro ao enviar notificação: " + e.getMessage());
        }
    }
}


