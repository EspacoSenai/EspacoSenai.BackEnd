package com.api.reserva.service;

import com.api.reserva.config.websocket.NotificacaoWebSocketHandler;
import com.api.reserva.dto.NotificacaoDTO;
import com.api.reserva.entity.Notificacao;
import com.api.reserva.entity.Usuario;
import com.api.reserva.event.NotificacaoEvent;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.NotificacaoRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.MetodosAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, UsuarioRepository usuarioRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Conta quantas notificações não lidas um usuário possui
     *
     * @param usuarioId ID do usuário
     * @return Quantidade de notificações não lidas
     */
    public Integer contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countNaoLidasByUsuarioId(usuarioId);
    }

    @Transactional
    public void novaNotificacao(Usuario usuario, String titulo, String mensagem) {
        if (usuario == null) {
            logger.warn("Tentativa de criar notificação sem usuário destino. titulo='{}' mensagem='{}'", titulo, mensagem);
            return;
        }

        // Criar entidade
        Notificacao notificacao = new Notificacao(
                usuario,
                titulo,
                mensagem
        );
        notificacao.setCriadoEm(java.time.LocalDateTime.now());

        // Persistir no banco de dados
        notificacaoRepository.save(notificacao);

        logger.info("✓ Notificação criada - ID: {}, Usuário: {}, Título: {}",
                notificacao.getId(), usuario.getId(), titulo);

        // Disparar evento imediatamente (async handlers)
        eventPublisher.publishEvent(new NotificacaoEvent(this, notificacao, usuario.getId()));
    }

    @Transactional
    public void lerNotificacao(Authentication authentication, Long id) {
        Long usuarioId = MetodosAuth.extrairId(authentication);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() ->
                new SemResultadosException("Usuário não encontrado"));

        Notificacao notificacao = notificacaoRepository.findById(id).orElseThrow(SemResultadosException::new);

        if (!notificacao.getUsuario().equals(usuario)) {
            throw new SemPermissaoException("Você não tem permissão para ler esta notificação");
        }

        notificacao.ler();
        notificacaoRepository.save(notificacao);
        logger.info("✓ Notificação marcada como lida - ID: {}, Usuário: {}", id, usuario.getId());
    }

    @Transactional
    public void deletarNotificacao(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id).orElseThrow(SemResultadosException::new);
        notificacaoRepository.delete(notificacao);
    }

    @Transactional
    public void deletarNotificacaoSeguro(Authentication authentication, Long id) {
        Long usuarioId = MetodosAuth.extrairId(authentication);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() ->
                new SemResultadosException("Usuário não encontrado"));

        Notificacao notificacao = notificacaoRepository.findById(id).orElseThrow(SemResultadosException::new);

        // Validação de segurança: só o dono pode deletar
        if (!notificacao.getUsuario().equals(usuario)) {
            logger.warn("⚠️ Tentativa não autorizada de deletar notificação - Usuário: {}, Notificação: {}",
                    usuario.getId(), id);
            throw new SemPermissaoException("Você não tem permissão para deletar esta notificação");
        }

        notificacaoRepository.delete(notificacao);
        logger.info("✓ Notificação deletada com segurança - ID: {}, Usuário: {}", id, usuario.getId());
    }

    /**
     * Busca notificações do usuário autenticado
     * Extrai o usuário do Authentication para seguir padrão do projeto
     *
     * @param authentication Authentication do usuário logado
     * @return Lista de notificações do usuário
     */
    public List<NotificacaoDTO> buscarMinhas(Authentication authentication) {
        Long usuarioId = MetodosAuth.extrairId(authentication);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() ->
                new SemResultadosException("Usuário não encontrado"));

        return notificacaoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(NotificacaoDTO::new)
                .toList();
    }


    /**
     * Enviar notificação em broadcast para todos os usuários
     *
     * @param dto Dados da notificação para broadcast
     */
    public void enviarBroadcast(NotificacaoDTO dto) {
        logger.info("📢 Iniciando broadcast: {}", dto.getTitulo());
        try {
            String json = objectMapper.writeValueAsString(dto);
            NotificacaoWebSocketHandler.enviarNotificacaoParaTodos(json);
            logger.info("✅ Broadcast enviado com sucesso: {}", dto.getTitulo());
        } catch (IOException e) {
            logger.error("❌ Erro ao enviar broadcast: {}", e.getMessage());
        }
    }

    /**
     * Enviar notificação via WebSocket para um usuário específico
     *
     * @param usuarioId ID do usuário
     * @param titulo Título da notificação
     * @param mensagem Mensagem da notificação
     */
    public void enviarNotificacaoWebSocket(Long usuarioId, String titulo, String mensagem) {
        logger.info("📨 Enviando notificação WebSocket para usuário {}: {}", usuarioId, titulo);
        try {
            NotificacaoDTO dto = new NotificacaoDTO(null, usuarioId, titulo, mensagem, java.time.LocalDateTime.now(), false);
            String json = objectMapper.writeValueAsString(dto);
            NotificacaoWebSocketHandler.enviarNotificacaoParaUsuario(usuarioId, json);
            logger.info("✅ Notificação enviada via WebSocket para usuário {}", usuarioId);
        } catch (IOException e) {
            logger.error("❌ Erro ao enviar notificação via WebSocket para usuário {}: {}", usuarioId, e.getMessage());
        }
    }

    /**
     * Reenviar última notificação para um usuário
     *
     * @param usuarioId ID do usuário
     */
    public void reenviarUltimaNotificacao(Long usuarioId) {
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioId(usuarioId);
        notificacoes.stream().findFirst().ifPresent(notificacao ->
                eventPublisher.publishEvent(new NotificacaoEvent(this, notificacao, usuarioId))
        );
    }
}