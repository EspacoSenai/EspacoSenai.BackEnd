package com.api.reserva.entity;

import com.api.reserva.enums.Agendamento;
import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.Disponibilidade;
import jakarta.persistence.*;

@Entity
public class Catalogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_ambiente", nullable = false)
    private Ambiente ambiente;

    @ManyToOne
    @JoinColumn(name = "id_periodo")
    private Periodo periodo;

    @ManyToOne
    @JoinColumn(name = "id_horario")
    private Horario horario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Agendamento agendamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana diaSemana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Disponibilidade disponibilidade;
    
    public Catalogo() {
    }

    public Catalogo(Ambiente ambiente, Periodo periodo, Horario horario, Agendamento agendamento,
                    DiaSemana diaSemana, Disponibilidade disponibilidade) {
        this.ambiente = ambiente;
        this.periodo = periodo;
        this.horario = horario;
        this.agendamento = agendamento;
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

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

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
}
