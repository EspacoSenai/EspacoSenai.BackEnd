package com.api.reserva.service;

import com.api.reserva.dto.NotificacaoDTO;
import com.api.reserva.entity.Notificacao;
import com.api.reserva.entity.Usuario;
import com.api.reserva.event.NotificacaoEvent;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.NotificacaoRepository;
import com.api.reserva.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

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
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            throw new SemResultadosException("Usuário não encontrado");
        }

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

    /**
     * Deletar notificação com validação de segurança
     * Valida se o usuário autenticado é o dono da notificação
     *
     * @param authentication Authentication do usuário
     * @param id ID da notificação
     */
    @Transactional
    public void deletarNotificacaoSeguro(Authentication authentication, Long id) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            throw new SemResultadosException("Usuário não encontrado");
        }

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
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            throw new SemResultadosException("Usuário não encontrado");
        }

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
        logger.info("📢 Broadcast enviado: {}", dto.getTitulo());
        // Implementação do broadcast via WebSocket
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