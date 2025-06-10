package com.api.reserva.entity;

import com.api.reserva.dto.HorarioDTO;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "tb_horario")
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalTime horaInicio;
    @Column(nullable = false)
    private LocalTime horaFim;

    public Horario() {}

    public Horario(LocalTime horaInicio, LocalTime horaFim) {
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Horario(HorarioDTO hour) {
        id = hour.getId();
        horaInicio = hour.getHoraInicio();
        horaFim = hour.getHoraFim();
    }

    public Long getId() {
        return id;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
}
