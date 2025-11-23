package com.api.reserva.service;

import com.api.reserva.entity.Recurso;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tb_reservas_recursos")
public class ReservaRecurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Recurso recurso;

    private LocalDate dataReserva;

    private LocalTime horaInicio;

    private LocalTime horaFim;

    public ReservaRecurso() {
    }

    public ReservaRecurso(Recurso recurso, LocalDate dataReserva, LocalTime horaInicio, LocalTime horaFim) {
        this.recurso = recurso;
        this.dataReserva = dataReserva;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Long getId() {
        return id;
    }

    public Recurso getAmbienteRecurso() {
        return recurso;
    }

    public void setAmbienteRecurso(Recurso recurso) {
        this.recurso = recurso;
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
