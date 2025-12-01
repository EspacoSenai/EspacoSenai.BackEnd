package com.api.reserva.dto;

import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.StatusReserva;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public class ReservaDTO {

    private Long id;

    private Long hostId;

    // Lista de IDs dos convidados vinculados à reserva
    private Set<Long> membrosIds;

    @NotNull(message = "Informe a Catalogo desejado.")
    private Long catalogoId;

    @NotNull(message = "Informe a data para a Reserva.")
    private LocalDate data;

    @NotNull(message = "Informe o horário de início da reserva.")

    private LocalTime horaInicio;

    @NotNull(message = "Informe o horário de término da reserva.")
    private LocalTime horaFim;

    private StatusReserva statusReserva;

    @Size(max = 500)
    private String finalidade;

    private LocalDateTime criadoEm;

    public ReservaDTO() {
    }

    public ReservaDTO(Reserva reserva) {
        this.id = reserva.getId();
        this.hostId = reserva.getHost().getId();
        this.catalogoId = reserva.getCatalogo().getId();
        this.data = reserva.getData();
        this.horaInicio = reserva.getHoraInicio();
        this.horaFim = reserva.getHoraFim();
        this.statusReserva = reserva.getStatusReserva();
        this.finalidade = reserva.getFinalidade();
        this.criadoEm = reserva.getCriadoEm();
        this.membrosIds = reserva.getMembros().stream().map(membro ->
                membro.getId()).collect(java.util.stream.Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public Long getHostId() {
        return hostId;
    }

    public Set<Long> getMembrosIds() {
        return membrosIds;
    }

    public void setMembrosIds(Set<Long> membrosIds) {
        this.membrosIds = membrosIds;
    }

    public Long getCatalogoId() {
        return catalogoId;
    }

    public void setCatalogoId(Long catalogoId) {
        this.catalogoId = catalogoId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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

    public StatusReserva getStatusReserva() {
        return statusReserva;
    }

    public void setStatusReserva(StatusReserva statusReserva) {
        this.statusReserva = statusReserva;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}


