package com.api.reserva.dto;

import com.api.reserva.entity.GradeAmbiente;
import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.StatusReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservaReferenciaDTO {
    private Long id;
    private Usuario usuario;
    private GradeAmbiente gradeAmbiente;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusReserva statusReserva;
    private String msgUsuario;
    private String msgInterna;
    private LocalDateTime dataHoraSolicitacao;

    public ReservaReferenciaDTO() {
    }

    public ReservaReferenciaDTO(Reserva reserva) {
        id = reserva.getId();
        usuario = reserva.getUsuario();
        gradeAmbiente = reserva.getGradeAmbiente();
        data = reserva.getData();
        horaInicio = reserva.getHoraInicio();
        horaFim = reserva.getHoraFim();
        statusReserva = reserva.getStatusReserva();
        msgUsuario = reserva.getMsgUsuario();
        msgInterna = reserva.getMsgInterna();
        dataHoraSolicitacao = reserva.getDataHoraSolicitacao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
