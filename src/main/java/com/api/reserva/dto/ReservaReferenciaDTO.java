package com.api.reserva.dto;

import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.StatusReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public class ReservaReferenciaDTO {
    private Long id;
    private UsuarioReferenciaDTO host;
    private CatalogoReferenciaDTO catalogo;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusReserva statusReserva;
    private String msgUsuario;
    private String msgInterna;
    private LocalDateTime dataHoraSolicitacao;
    private Set<UsuarioReferenciaDTO> membros;

    public ReservaReferenciaDTO() {
    }

    public ReservaReferenciaDTO(Reserva reserva) {
        id = reserva.getId();
        host = new UsuarioReferenciaDTO(reserva.getHost());
        catalogo = new CatalogoReferenciaDTO(reserva.getCatalogo());
        data = reserva.getData();
        horaInicio = reserva.getHoraInicio();
        horaFim = reserva.getHoraFim();
        statusReserva = reserva.getStatusReserva();
        msgUsuario = reserva.getMsgUsuario();
        msgInterna = reserva.getMsgInterna();
        membros = reserva.getMembros().stream().map(UsuarioReferenciaDTO::new).collect(java.util.stream.Collectors.toSet());
        dataHoraSolicitacao = reserva.getDataHoraSolicitacao();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioReferenciaDTO getHost() {
        return host;
    }

    public void setHost(UsuarioReferenciaDTO host) {
        this.host = host;
    }

    public Set<UsuarioReferenciaDTO> getMembros() {
        return membros;
    }

    public void setMembros(Set<UsuarioReferenciaDTO> membros) {
        this.membros = membros;
    }

    public CatalogoReferenciaDTO getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(CatalogoReferenciaDTO catalogo) {
        this.catalogo = catalogo;
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

    public String getMsgUsuario() {
        return msgUsuario;
    }

    public void setMsgUsuario(String msgUsuario) {
        this.msgUsuario = msgUsuario;
    }

    public String getMsgInterna() {
        return msgInterna;
    }

    public void setMsgInterna(String msgInterna) {
        this.msgInterna = msgInterna;
    }

    public LocalDateTime getDataHoraSolicitacao() {
        return dataHoraSolicitacao;
    }

    public void setDataHoraSolicitacao(LocalDateTime dataHoraSolicitacao) {
        this.dataHoraSolicitacao = dataHoraSolicitacao;
    }
}
