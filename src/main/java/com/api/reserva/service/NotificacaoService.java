package com.api.reserva.service;

import com.api.reserva.dto.NotificacaoDTO;
import com.api.reserva.entity.Notificacao;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.NotificacaoTipo;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.NotificacaoRepository;
import com.api.reserva.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<NotificacaoDTO> buscar() {
        return notificacaoRepository.findAll()
                .stream()
                .map(NotificacaoDTO::new)
                .toList();
    }

    public NotificacaoDTO buscar(Long id) {
        return new NotificacaoDTO(notificacaoRepository.findById(id).orElseThrow(SemPermissaoException::new));
    }

    @Transactional
    public void novaNotificacao(Usuario usuario, NotificacaoTipo notificacaoTipo, String titulo, String mensagem) {
        if (usuario == null) {
            // Não lançar exceção para não interromper fluxos críticos; registramos para investigação
            logger.warn("Tentativa de criar notificação sem usuário destino. titulo='{}' mensagem='{}' tipo={}", titulo, mensagem, notificacaoTipo);
            return;
        }
        Notificacao notificacao = new Notificacao(
                usuario,
                notificacaoTipo,
                titulo,
                mensagem
        );
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void lerNotificacao(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id).orElseThrow(SemResultadosException::new);
        notificacao.ler();
    }

    @Transactional
    public void deletarNotificacao(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id).orElseThrow(SemResultadosException::new);
        notificacaoRepository.delete(notificacao);
    }
}
