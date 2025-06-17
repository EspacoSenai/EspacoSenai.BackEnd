package com.api.reserva.entity;

import com.api.reserva.dto.GradeAmbienteDTO;
import com.api.reserva.enums.Agendamento;
import com.api.reserva.enums.Disponibilidade;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class GradeAmbiente {
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
    private Disponibilidade disponibilidade;
    
    public GradeAmbiente() {
    }

    public GradeAmbiente(Ambiente ambiente, Periodo periodo, Horario horario, Agendamento agendamento,
                         Disponibilidade disponibilidade) {
        this.ambiente = ambiente;
        this.periodo = periodo;
        this.horario = horario;
        this.agendamento = agendamento;
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

    public Disponibilidade getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Disponibilidade disponibilidade) {
        this.disponibilidade = disponibilidade;
    }
}
