package com.api.reserva.dto;

import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.StatusReserva;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservaDTO {

    private Long id;

    @NotNull(message = "Informe o usuário que será vinculado a essa Reserva.")
    private Long idUsuario;

    @NotNull(message = "Informe a Grade.")
    private Long idGradeAmbiente;

    @NotNull(message = "Informe a data para a Reserva.")
    private LocalDate data;

    @NotNull(message = "Informe o horário de início da reserva.")
    private LocalTime horaInicio;

    @NotNull(message = "Informe o horário de término da reserva.")
    private LocalTime horaFim;

    private StatusReserva statusReserva;

    @Size(max = 255)
    private String msgUsuario;

    @Size(max = 255)
    private String msgInterna;

    private LocalDateTime dataHoraSolicitacao;

    public ReservaDTO() {
    }

    public ReservaDTO(Reserva reserva) {
        id = reserva.getId();
        idUsuario = reserva.getUsuario().getId();
        idGradeAmbiente = reserva.getGradeAmbiente().getId();
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

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdGradeAmbiente() {
        return idGradeAmbiente;
    }

    public void setIdGradeAmbiente(Long idGradeAmbiente) {
        this.idGradeAmbiente = idGradeAmbiente;
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
