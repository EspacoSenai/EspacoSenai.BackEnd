package com.api.reserva.dto;

import com.api.reserva.entity.Catalogo;
import com.api.reserva.entity.Horario;
import com.api.reserva.enums.Agendamento;
import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.Disponibilidade;

public class CatalogoReferenciaDTO {
    private Long id;
    private AmbienteReferenciaDTO ambiente;
    private Agendamento agendamento;
    private DiaSemana diaSemana;
    private Horario horario;
    private Disponibilidade disponibilidade;

    public CatalogoReferenciaDTO() {
    }

    public CatalogoReferenciaDTO(Long id, AmbienteReferenciaDTO ambiente, Agendamento agendamento,
                                 DiaSemana diaSemana, Horario horario, Disponibilidade disponibilidade) {
        this.id = id;
        this.ambiente = ambiente;
        this.agendamento = agendamento;
        this.diaSemana = diaSemana;
        this.horario = horario;
        this.disponibilidade = disponibilidade;
    }

    public CatalogoReferenciaDTO(Catalogo catalogo) {
        id = catalogo.getId();
        ambiente = new AmbienteReferenciaDTO(catalogo.getAmbiente());
        horario = catalogo.getHorario();
        agendamento = catalogo.getAgendamento();
        diaSemana = catalogo.getDiaSemana();
        disponibilidade = catalogo.getDisponibilidade();
    }

    public Long getId() {
        return id;
    }

    public AmbienteReferenciaDTO getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(AmbienteReferenciaDTO ambiente) {
        this.ambiente = ambiente;
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
