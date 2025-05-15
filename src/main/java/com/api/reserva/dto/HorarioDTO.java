package com.api.reserva.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalTime;

public class HorarioDTO {
    private Long id;
    @NotBlank(message = "O horário de inicio é obrigatório.")
    private LocalTime horaInicio;
    @NotBlank(message = "O horário de término é obrigatório.")
    private LocalTime horaFim;

    public HorarioDTO() {
    }

    public HorarioDTO(LocalTime horaInicio, LocalTime horaFim) {
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public HorarioDTO(Long id, LocalTime horaInicio, LocalTime horaFim) {
        this.id = id;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Long getId() {
        return id;
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
