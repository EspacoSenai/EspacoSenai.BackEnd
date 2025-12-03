package com.api.reserva.event;

import com.api.reserva.entity.Notificacao;
import org.springframework.context.ApplicationEvent;

/**
 * Evento disparado quando uma notificação é criada.
 * Permite que listeners reajam imediatamente ao evento de criação.
 */
public class NotificacaoEvent extends ApplicationEvent {
    private final Notificacao notificacao;
    private final Long usuarioDestinatarioId;

    public NotificacaoEvent(Object source, Notificacao notificacao, Long usuarioDestinatarioId) {
        super(source);
        this.notificacao = notificacao;
        this.usuarioDestinatarioId = usuarioDestinatarioId;
    }

    public Notificacao getNotificacao() {
        return notificacao;
    }

    public Long getUsuarioDestinatarioId() {
        return usuarioDestinatarioId;
    }

    @Override
    public String toString() {
        return "NotificacaoEvent{" +
                "notificacao=" + notificacao.getId() +
                ", usuarioDestinatarioId=" + usuarioDestinatarioId +
                '}';
    }
}

