package com.api.reserva.dto;

import com.api.reserva.entity.Periodo;
import com.api.reserva.enums.PeriodoAmbiente;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class PeriodoDTO {
    private Long id;

    @NotNull(message = "Escolha um período.")
    private PeriodoAmbiente periodoAmbiente;
    @NotNull(message = "O horário de início é obrigatório.")
    private LocalTime horaInicio;
    @NotNull(message = "O horário de término é obrigatório.")
    private LocalTime horaFim;

    public PeriodoDTO() {}

    public PeriodoDTO(PeriodoAmbiente periodoAmbiente, LocalTime horaInicio, LocalTime horaFim) {
        this.periodoAmbiente = periodoAmbiente;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public PeriodoDTO(Periodo periodo) {
        id = periodo.getId();
        periodoAmbiente = periodo.getPeriodoAmbiente();
        horaInicio = periodo.getHoraInicio();
        horaFim = periodo.getHoraFim();
    }

    public Long getId() {
        return id;
    }

    public PeriodoAmbiente getPeriodoAmbiente() {
        return periodoAmbiente;
    }

    public void setPeriodoAmbiente(PeriodoAmbiente periodoAmbiente) {
        this.periodoAmbiente = periodoAmbiente;
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
}
