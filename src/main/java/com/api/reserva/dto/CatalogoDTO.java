package com.api.reserva.dto;

import com.api.reserva.entity.Catalogo;
import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.Disponibilidade;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class CatalogoDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Escolha um ambiente para vincular a esta grade.")
    private Long idAmbiente;

//    private Long idHorario;

    @NotNull(message = "Defina a partir de que horário o ambiente ficará disponível.")
    private LocalTime horaInicio;

    @NotNull(message = "Defina a partir de que horário o ambiente ficará indisponível.")
    private LocalTime horaFim;

//    @NotNull(message = "Indique se a grade usa um PERÍODO ou HORÁRIO.")
//    private Agendamento agendamento;

    @NotNull(message = "Escolha um dia da semana.")
    private DiaSemana diaSemana;

    @NotNull(message = "Indique a disponibilidade desta grade atualmente.")
    private Disponibilidade disponibilidade;

    public CatalogoDTO() {
    }

    public CatalogoDTO(Long idAmbiente, Long idPeriodo, LocalTime horaInicio, LocalTime horaFim
                       ,DiaSemana diaSemana, Disponibilidade disponibilidade) {
        this.idAmbiente = idAmbiente;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.diaSemana = diaSemana;
        this.disponibilidade = disponibilidade;
    }

    public CatalogoDTO(Catalogo catalogo) {
        id = catalogo.getId();
        idAmbiente = catalogo.getAmbiente().getId();
//        idHorario = catalogo.getId();
        diaSemana = catalogo.getDiaSemana();
        disponibilidade = catalogo.getDisponibilidade();
        horaInicio = catalogo.getHoraInicio();
        horaFim = catalogo.getHoraFim();
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

//    public Long getIdPeriodo() {
//        return idPeriodo;
//    }
//
//    public void setIdPeriodo(Long idPeriodo) {
//        this.idPeriodo = idPeriodo;
//    }

//    public Long getIdHorario() {
//        return idHorario;
//    }
//
//    public void setIdHorario(Long idHorario) {
//        this.idHorario = idHorario;
//    }


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
