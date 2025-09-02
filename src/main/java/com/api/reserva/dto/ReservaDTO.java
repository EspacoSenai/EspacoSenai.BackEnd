package com.api.reserva.dto;

import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.StatusReserva;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public class ReservaDTO {

    private Long id;

    @NotNull(message = "Informe o usuário que será vinculado a essa Reserva.")
    private Long hostId;

    // Lista de IDs dos convidados vinculados à reserva
    private Set<Long> convidadosIds;

    @NotNull(message = "Informe a Catalogo desejado.")
    private Long catalogoId;

    @NotNull(message = "Informe a data para a Reserva.")
    @Future(message = "A data da reserva deve ser futura.")
    private LocalDate data;

    @NotNull(message = "Informe o horário de início da reserva.")
    private LocalTime horaInicio;

    @NotNull(message = "Informe o horário de término da reserva.")
    private LocalTime horaFim;

    private StatusReserva statusReserva;

    @Size(max = 500)
    private String msgUsuario;

    @Size(max = 500)
    private String msgInterna;

    private LocalDateTime dataHoraSolicitacao;

    public ReservaDTO() {
    }

    public ReservaDTO(Reserva reserva) {
        this.id = reserva.getId();
        this.hostId = reserva.getHost().getId();
        this.catalogoId = reserva.getCatalogo().getId();
        this.data = reserva.getData();
        this.horaInicio = reserva.getHoraInicio();
        this.horaFim = reserva.getHoraFim();
        this.statusReserva = reserva.getStatusReserva();
        this.msgUsuario = reserva.getMsgUsuario();
        this.msgInterna = reserva.getMsgInterna();
        this.dataHoraSolicitacao = reserva.getDataHoraSolicitacao();
    }

    public Long getId() {
        return id;
    }

    public Long getHostId() {
        return hostId;
    }

    public Set<Long> getConvidadosIds() {
        return convidadosIds;
    }

    public void setConvidadosIds(Set<Long> convidadosIds) {
        this.convidadosIds = convidadosIds;
    }

    public Long getCatalogoId() {
        return catalogoId;
    }

    public void setCatalogoId(Long catalogoId) {
        this.catalogoId = catalogoId;
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
