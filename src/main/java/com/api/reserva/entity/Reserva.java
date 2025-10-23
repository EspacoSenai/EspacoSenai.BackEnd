package com.api.reserva.entity;

import com.api.reserva.enums.StatusReserva;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private Usuario host;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservaConvidados> convidados;

    @ManyToOne
    @JoinColumn(name = "catalogo_id")
    private Catalogo catalogo;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    private StatusReserva statusReserva;

    @Column(length = 500)
    private String msgUsuario;

    @Column(length = 500)
    private String msgInterna;

    private LocalDateTime dataHoraSolicitacao;

    public Reserva() {
    }

    public Reserva(Usuario host, Catalogo catalogo, LocalDate data, LocalTime horaInicio,
                   LocalTime horaFim, StatusReserva statusReserva, String msgUsuario, String msgInterna,
                   LocalDateTime dataHoraSolicitacao) {
        this.host = host;
        this.catalogo = catalogo;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.statusReserva = statusReserva;
        this.msgUsuario = msgUsuario;
        this.msgInterna = msgInterna;
        this.dataHoraSolicitacao = dataHoraSolicitacao;
    }

    public Long getId() {
        return id;
    }

    public Usuario getHost() {
        return host;
    }

    public void setHost(Usuario host) {
        this.host = host;
    }

    public Catalogo getGradeAmbiente() {
        return catalogo;
    }

    public void setGradeAmbiente(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public StatusReserva getStatusReserva() {
        return statusReserva;
    }

    public void setStatusReserva(StatusReserva statusReserva) {
        this.statusReserva = statusReserva;
    }

    public String getMsgUsuario() {
        return msgUsuario;
    }

    public void setMsgUsuario(String msgUsuario) {
        this.msgUsuario = msgUsuario;
    }

    public String getMsgInterna() {
        return msgInterna;
    }

    public void setMsgInterna(String msgInterna) {
        this.msgInterna = msgInterna;
    }

    public LocalDateTime getDataHoraSolicitacao() {
        return dataHoraSolicitacao;
    }

    public void setDataHoraSolicitacao(LocalDateTime dataHoraSolicitacao) {
        this.dataHoraSolicitacao = dataHoraSolicitacao;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public Set<ReservaConvidados> getConvidados() {
        return convidados;
    }

    public void setConvidados(Set<ReservaConvidados> convidados) {
        this.convidados = convidados;
    }
}
