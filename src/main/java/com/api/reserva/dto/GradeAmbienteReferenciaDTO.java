package com.api.reserva.dto;

import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.GradeAmbiente;
import com.api.reserva.entity.Horario;
import com.api.reserva.entity.Periodo;
import com.api.reserva.enums.Agendamento;
import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.Disponibilidade;

public class GradeAmbienteReferenciaDTO {
    private Long id;
    private Ambiente ambiente;
    private Agendamento agendamento;
    private DiaSemana diaSemana;
    private Periodo periodo;
    private Horario horario;
    private Disponibilidade disponibilidade;

    public GradeAmbienteReferenciaDTO() {
    }

    public GradeAmbienteReferenciaDTO(Long id, Ambiente ambiente, Periodo periodo, Horario horario,
                                      Agendamento agendamento, DiaSemana diaSemana, Disponibilidade disponibilidade) {
        this.id = id;
        this.ambiente = ambiente;
        this.periodo = periodo;
        this.horario = horario;
        this.agendamento = agendamento;
        this.diaSemana = diaSemana;
        this.disponibilidade = disponibilidade;
    }

    public GradeAmbienteReferenciaDTO(GradeAmbiente gradeAmbiente) {
        id = gradeAmbiente.getId();
        ambiente = gradeAmbiente.getAmbiente();
        periodo = gradeAmbiente.getPeriodo();
        horario = gradeAmbiente.getHorario();
        agendamento = gradeAmbiente.getAgendamento();
        diaSemana = gradeAmbiente.getDiaSemana();
        disponibilidade = gradeAmbiente.getDisponibilidade();
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
