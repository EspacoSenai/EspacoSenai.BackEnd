package com.api.reserva.dto;

import com.api.reserva.entity.Notificacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class NotificacaoDTO{
    private Long id;

    private Long usuarioId;

    @NotNull
    @Size(message = "O título deve ter no máximo 100 caracteres.", max = 100)
    private String titulo;

    @Size(message = "A mensagem deve ter no máximo 500 caracteres.", max = 500)
    private String mensagem;
    private LocalDateTime criadoEm;
    private boolean lida;

    public NotificacaoDTO(Long id, String titulo, String mensagem, LocalDateTime criadoEm, boolean lida) {
        this.id = id;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.criadoEm = criadoEm;
        this.lida = lida;
    }

    public NotificacaoDTO(Notificacao notificacao) {
        id = notificacao.getId();
        titulo = notificacao.getTitulo();
        mensagem = notificacao.getMensagem();
        criadoEm = notificacao.getDataHoraCriacao();
        lida = notificacao.isLida();
    }

    public Long getId() {
        return id;
    }


    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public boolean getLida() {
        return lida;
    }

}