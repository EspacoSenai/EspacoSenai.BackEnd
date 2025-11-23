package com.api.reserva.entity;

import com.api.reserva.enums.NotificacaoTipo;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_notificacao")
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private NotificacaoTipo notificacaoTipo;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    private LocalDateTime dataHoraCriacao;

    private boolean lida;

    public Notificacao() {
    }

    public Notificacao(Usuario usuario, NotificacaoTipo notificacaoTipo, String titulo, String mensagem) {
        this.usuario = usuario;
        this.notificacaoTipo = notificacaoTipo;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.dataHoraCriacao = LocalDateTime.now();
        this.lida = false;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public NotificacaoTipo getNotificacaoTipo() {
        return notificacaoTipo;
    }

    public void setNotificacaoTipo(NotificacaoTipo notificacaoTipo) {
        this.notificacaoTipo = notificacaoTipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(LocalDateTime dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public void ler() {
        this.lida = true;
    }
}
