package com.api.reserva.entity;

import com.api.reserva.dto.PeriodoDTO;
import com.api.reserva.enums.PeriodoAmbiente;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "tb_periodo")
public class Periodo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PeriodoAmbiente periodoAmbiente;
    @Column(nullable = false)
    private LocalTime horaInicio;
    @Column(nullable = false)
    private LocalTime horaFim;

    public Periodo(){}

    public Periodo(PeriodoAmbiente periodoAmbiente, LocalTime horaInicio, LocalTime horaFim) {
        this.periodoAmbiente = periodoAmbiente;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Periodo(PeriodoDTO periodoDTO) {
        this.id = periodoDTO.getId();
        this.periodoAmbiente = periodoDTO.getPeriodoAmbiente();
        this.horaInicio = periodoDTO.getHoraInicio();
        this.horaFim = periodoDTO.getHoraFim();
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

    public void setHoraInicio(LocalTime inicio) {
        this.horaInicio = inicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime termina) {
        this.horaFim = termina;
    }
}
