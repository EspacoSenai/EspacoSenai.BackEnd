package com.api.reserva.dto;

import com.api.reserva.entity.Catalogo;
import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.Disponibilidade;

import java.time.LocalTime;

public class CatalogoReferenciaDTO {
    private Long id;
    private Long ambienteId;  // ← Apenas ID, não o objeto completo
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Disponibilidade disponibilidade;

    public CatalogoReferenciaDTO() {
    }

    public CatalogoReferenciaDTO(Long id, Long ambienteId, DiaSemana diaSemana,
                                 LocalTime horaInicio, LocalTime horaFim, Disponibilidade disponibilidade) {
        this.id = id;
        this.ambienteId = ambienteId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.disponibilidade = disponibilidade;
    }

    public CatalogoReferenciaDTO(Catalogo catalogo) {
        id = catalogo.getId();
        ambienteId = catalogo.getAmbiente().getId();  // ← Apenas ID
        diaSemana = catalogo.getDiaSemana();
        horaInicio = catalogo.getHoraInicio();
        horaFim = catalogo.getHoraFim();
        disponibilidade = catalogo.getDisponibilidade();
    }

    public Long getId() {
        return id;
    }

    public Long getAmbienteId() {
        return ambienteId;
    }

    public void setAmbienteId(Long ambienteId) {
        this.ambienteId = ambienteId;
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
