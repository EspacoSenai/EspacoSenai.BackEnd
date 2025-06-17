package com.api.reserva.entity;

import com.api.reserva.enums.StatusReserva;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tb_reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idGradeAmbiente", nullable = false)
    private GradeAmbiente gradeAmbiente;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;
    @Column(nullable = false)
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    private StatusReserva statusReserva;

    @Column(length = 255)
    private String msgUsuario;

    @Column(length = 255)
    private String msgInterna;

    private LocalDateTime dataHoraSolicitacao;

    public Reserva() {
    }

    public Reserva(Usuario usuario, GradeAmbiente gradeAmbiente, LocalDate data, LocalTime horaInicio,
                   LocalTime horaFim, StatusReserva statusReserva, String msgUsuario, String msgInterna,
                   LocalDateTime dataHoraSolicitacao) {
        this.usuario = usuario;
        this.gradeAmbiente = gradeAmbiente;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public GradeAmbiente getGradeAmbiente() {
        return gradeAmbiente;
    }

    public void setGradeAmbiente(GradeAmbiente gradeAmbiente) {
        this.gradeAmbiente = gradeAmbiente;
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
}
