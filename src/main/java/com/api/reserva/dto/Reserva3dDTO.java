package com.api.reserva.dto;

import com.api.reserva.entity.ReservaImpressora;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.StatusReserva3D;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Reserva3dDTO {
    private Long id;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusReserva3D statusReserva;
    private LocalDateTime dataHoraSolicitacao;
    private Integer pin;

    public Reserva3dDTO() {
    }

    public Reserva3dDTO(ReservaImpressora reserva) {
        this.id = id;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.statusReserva = statusReserva;
        this.dataHoraSolicitacao = dataHoraSolicitacao;
        this.pin = pin;

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

    public StatusReserva3D getStatusReserva() {
        return statusReserva;
    }

    public void setStatusReserva(StatusReserva3D statusReserva) {
        this.statusReserva = statusReserva;
    }

    public LocalDateTime getDataHoraSolicitacao() {
        return dataHoraSolicitacao;
    }

    public void setDataHoraSolicitacao(LocalDateTime dataHoraSolicitacao) {
        this.dataHoraSolicitacao = dataHoraSolicitacao;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }


}