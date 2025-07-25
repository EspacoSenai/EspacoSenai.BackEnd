package com.api.reserva.dto;

import com.api.reserva.entity.Catalogo;
import com.api.reserva.enums.Agendamento;
import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.Disponibilidade;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

public class CatalogoDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Escolha um ambiente para vincular a esta grade.")
    private Long idAmbiente;

    private Long idPeriodo;

    private Long idHorario;

    @NotNull(message = "Indique se a grade usa um PERÍODO ou HORÁRIO.")
    private Agendamento agendamento;

    @NotNull(message = "Escolha um dia da semana.")
    private DiaSemana diaSemana;

    @NotNull(message = "Indique a disponibilidade desta grade atualmente.")
    private Disponibilidade disponibilidade;

    public CatalogoDTO() {
    }

    public CatalogoDTO(Long idAmbiente, Long idPeriodo, Long idHorario, Agendamento agendamento,
                       DiaSemana diaSemana , Disponibilidade disponibilidade) {
        this.idAmbiente = idAmbiente;
        this.idPeriodo = idPeriodo;
        this.idHorario = idHorario;
        this.agendamento = agendamento;
        this.disponibilidade = disponibilidade;
    }

    public CatalogoDTO(Catalogo catalogo) {
        id = catalogo.getId();
        idAmbiente = catalogo.getAmbiente().getId();
        idHorario = catalogo.getId();
        agendamento = catalogo.getAgendamento();
        diaSemana = catalogo.getDiaSemana();
        disponibilidade = catalogo.getDisponibilidade();
    }

    public Long getId() {
        return id;
    }

    public Long getIdAmbiente() {
        return idAmbiente;
    }

    public void setIdAmbiente(Long idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Long idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Long getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Long idHorario) {
        this.idHorario = idHorario;
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
