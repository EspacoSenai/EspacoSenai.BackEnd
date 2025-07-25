package com.api.reserva.entity;

import com.api.reserva.enums.StatusSuspensao;
import jakarta.persistence.*;

import java.util.Date;

@Table(name = "tb_estudante_suspensao")
public class EstudanteSuspensao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Data de início da suspensão (se não houver, suspender imediatamente.
     */
    private Date dataInicio;

    /**
     * Data de término da suspensão.
     */
    @Column(nullable = false)
    private Date dataFim;

    /**
     * Motivo da suspensão.
     */
    @Column(length = 500)
    private String motivo;

    /**
     * Status da suspensão (ativa, expirada, cancelada, aguardando).
     */
    @Enumerated(EnumType.STRING)
    private StatusSuspensao statusSuspensao;

    public EstudanteSuspensao() {
    }

    public EstudanteSuspensao(Usuario usuario, Date dataInicio, Date dataFim, String motivo, StatusSuspensao statusSuspensao) {
        this.usuario = usuario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.motivo = motivo;
        this.statusSuspensao = statusSuspensao;
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

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public StatusSuspensao getStatusSuspensao() {
        return statusSuspensao;
    }

    public void setStatusSuspensao(StatusSuspensao statusSuspensao) {
        this.statusSuspensao = statusSuspensao;
    }
}
