package com.api.reserva.dto;

import com.api.reserva.entity.ReservaImpressora;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.StatusReserva3D;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservaImpressoraReferenciaDTO {

    private Long id;
    private Long host;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalDateTime dataHoraSolicitacao;
    private StatusReserva3D statusReserva;
    private Integer pin;
    private Double temperatura;

    public ReservaImpressoraReferenciaDTO() {
    }

    public ReservaImpressoraReferenciaDTO(ReservaImpressora reserva) {
        this.id = reserva.getId();
        this.host = reserva.getHost().getId();
        this.data = reserva.getData();
        this.horaInicio = reserva.getHoraInicio();
        this.horaFim = reserva.getHoraFim();
        this.dataHoraSolicitacao = reserva.getDataHoraSolicitacao();
        this.statusReserva = reserva.getStatusReserva();
        this.pin = reserva.getPin();
        this.temperatura = reserva.getTemperatura();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getDataHoraSolicitacao() {
        return dataHoraSolicitacao;
    }

    public void setDataHoraSolicitacao(LocalDateTime dataHoraSolicitacao) { this.dataHoraSolicitacao = dataHoraSolicitacao; }

    public StatusReserva3D getStatusReserva() { return statusReserva; }

    public void setStatusReserva(StatusReserva3D statusReserva) { this.statusReserva = statusReserva; }

    public Integer getPin() { return pin; }

    public void setPin(Integer pin) { this.pin = pin; }

    public Double getTemperatura() { return temperatura; }

    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public Long getHost() { return host; }

    public void setHost(Long host) { this.host = host; }
}