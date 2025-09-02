package com.api.reserva.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reserva_convidados",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reserva_id", "convidado_id"}))
@IdClass(ReservaConvidados.ReservaConvidadoId.class)
public class ReservaConvidados {
    @Id
    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Id
    @ManyToOne
    @JoinColumn(name = "convidado_id", nullable = false)
    private Usuario convidado;

    @Column(name = "data_confirmacao")
    private LocalDateTime dataConfirmacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "convite_status", nullable = false)
    private ConviteStatus conviteStatus;

    public static class ReservaConvidadoId implements Serializable {
        private Long reserva;
        private Long convidado;

        public ReservaConvidadoId() {
        }

        public ReservaConvidadoId(Long reserva, Long convidado) {
            this.reserva = reserva;
            this.convidado = convidado;
        }

        public Long getConvidado() {
            return convidado;
        }

        public void setConvidado(Long convidado) {
            this.convidado = convidado;
        }

        public Long getReserva() {
            return reserva;
        }

        public void setReserva(Long reserva) {
            this.reserva = reserva;
        }
    }

    public ReservaConvidados() {
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Usuario getConvidado() {
        return convidado;
    }

    public void setConvidado(Usuario usuario) {
        this.convidado = usuario;
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }

    public void setDataConfirmacao(LocalDateTime dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }

    public ConviteStatus getStatus() {
        return conviteStatus;
    }

    public void setStatus(ConviteStatus conviteStatus) {
        this.conviteStatus = conviteStatus;
    }

    public enum ConviteStatus {
        ACEITO(1L),
        PENDENTE(2L),
        RECUSADO(3L);

        long conviteStatusId;

        ConviteStatus(long conviteStatusId) {
            this.conviteStatusId = conviteStatusId;
        }
    }
}
