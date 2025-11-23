package com.api.reserva.entity;

import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.Disponibilidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_catalogo")
public class Catalogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ambiente_id", nullable = false)
    private Ambiente ambiente;

//    @ManyToOne
//    @JoinColumn(name = "id_horario")
//    private Horario horario;

    @NotNull
    private LocalTime horaInicio;

    @NotNull
    private LocalTime horaFim;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Agendamento agendamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana diaSemana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Disponibilidade disponibilidade;

    @JsonIgnore
    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reserva> reservas = new HashSet<>();

    public Catalogo() {
    }

    public Catalogo(Ambiente ambiente, LocalTime horaInicio, LocalTime horaFim, DiaSemana diaSemana,
                    Disponibilidade disponibilidade) {
        this.ambiente = ambiente;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.diaSemana = diaSemana;
        this.disponibilidade = disponibilidade;
    }

    public Long getId() {
        return id;
    }

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
        this.ambiente = ambiente;
    }

//    public Horario getHorario() {
//        return horario;
//    }
//
//    public void setHorario(Horario horario) {
//        this.horario = horario;
//    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Disponibilidade getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Disponibilidade disponibilidade) {
        this.disponibilidade = disponibilidade;
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

    public Set<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(Set<Reserva> reservas) {
        this.reservas = reservas;
    }
}