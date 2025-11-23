package com.api.reserva.dto;

import com.api.reserva.entity.Notificacao;
import com.api.reserva.enums.NotificacaoTipo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class NotificacaoDTO{
    private Long id;

    private Long usuarioId;

    private NotificacaoTipo notificacaoTipo;

    @NotNull
    @Size(message = "O título deve ter no máximo 100 caracteres.", max = 100)
    private String titulo;

    @Size(message = "A mensagem deve ter no máximo 500 caracteres.", max = 500)
    private String mensagem;
    private LocalDateTime dataHoraCriacao;
    private boolean lida;

    public NotificacaoDTO(Long id, NotificacaoTipo notificacaoTipo, String titulo, String mensagem, LocalDateTime dataHoraCriacao, boolean lida) {
        this.id = id;
        this.notificacaoTipo = notificacaoTipo;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.dataHoraCriacao = dataHoraCriacao;
        this.lida = lida;
    }

    public NotificacaoDTO(Notificacao notificacao) {
        id = notificacao.getId();
        notificacaoTipo = notificacao.getNotificacaoTipo();
        titulo = notificacao.getTitulo();
        mensagem = notificacao.getMensagem();
        dataHoraCriacao = notificacao.getDataHoraCriacao();
        lida = notificacao.isLida();
    }

    public Long getId() {
        return id;
    }

    public NotificacaoTipo getNotificacaoTipo() {
        return notificacaoTipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public boolean getLida() {
        return lida;
    }

}