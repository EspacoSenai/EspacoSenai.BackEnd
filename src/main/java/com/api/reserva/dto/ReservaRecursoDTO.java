package com.api.reserva.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link com.api.reserva.service.ReservaRecurso}
 */
public class ReservaRecursoDTO {
    private Long id;
    private Long recursoId;
    private LocalDate dataReserva;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public ReservaRecursoDTO(Long id, Long recursoId, LocalDate dataReserva, LocalTime horaInicio, LocalTime horaFim) {
        this.id = id;
        this.recursoId = recursoId;
        this.dataReserva = dataReserva;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Long getId() {
        return id;
    }

    public Long getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Long recursoId) {
        this.recursoId = recursoId;
    }

    public LocalDate getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDate dataReserva) {
        this.dataReserva = dataReserva;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }
}