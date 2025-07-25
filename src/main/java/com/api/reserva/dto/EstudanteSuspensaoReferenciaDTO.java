package com.api.reserva.dto;

import com.api.reserva.entity.EstudanteSuspensao;
import com.api.reserva.enums.StatusSuspensao;

import java.util.Date;

public class EstudanteSuspensaoReferenciaDTO {
    private Long id;
    private UsuarioDTO estudante;
    private Date dataInicio;
    private Date dataFim;
    private String motivo;
    private StatusSuspensao statusSuspensao;

    public EstudanteSuspensaoReferenciaDTO() {
    }

    public EstudanteSuspensaoReferenciaDTO(EstudanteSuspensao estudanteSuspensao) {
        this.id = estudanteSuspensao.getId();
        this.estudante = new UsuarioDTO(estudanteSuspensao.getUsuario());
        this.dataInicio = estudanteSuspensao.getDataInicio();
        this.dataFim = estudanteSuspensao.getDataFim();
        this.motivo = estudanteSuspensao.getMotivo();
        this.statusSuspensao = estudanteSuspensao.getStatusSuspensao();
    }

    public UsuarioDTO getEstudante() {
        return estudante;
    }

    public Long getId() {
        return id;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public StatusSuspensao getStatusSuspensao() {
        return statusSuspensao;
    }
}