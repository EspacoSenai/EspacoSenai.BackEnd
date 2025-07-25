package com.api.reserva.dto;

import com.api.reserva.entity.EstudanteSuspensao;
import com.api.reserva.enums.StatusSuspensao;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class EstudanteSuspensaoDTO {

    private Long id;

    @NotNull(message = "Indique o Estudante a ser suspenso.")
    private Long estudanteId;

    @Future(message = "A data de início da suspensão deve ser futura.")
    private Date dataInicio;

    private Date dataFim;

    @Size(max = 500, message = "O motivo da suspensão deve ter no máximo 500 caracteres.")
    private String motivo;

    private StatusSuspensao statusSuspensao;

    public EstudanteSuspensaoDTO() {
    }

    public EstudanteSuspensaoDTO(Long estudanteId, Date dataInicio, Date dataFim, String motivo) {
        this.estudanteId = estudanteId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.motivo = motivo;
//        // Se não houver data de início, suspender imediatamente
//        if(dataInicio != null) {
//            this.statusSuspensao = StatusSuspensao.AGUARDANDO;
//        } else {
//            this.statusSuspensao = StatusSuspensao.ATIVA;
//        }
    }

    public EstudanteSuspensaoDTO(EstudanteSuspensao estudanteSuspensao) {
        this.id = estudanteSuspensao.getId();
        this.estudanteId = estudanteSuspensao.getUsuario().getId();
        this.dataInicio = estudanteSuspensao.getDataInicio();
        this.dataFim = estudanteSuspensao.getDataFim();
        this.motivo = estudanteSuspensao.getMotivo();
        this.statusSuspensao = estudanteSuspensao.getStatusSuspensao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEstudanteId() {
        return estudanteId;
    }

    public void setEstudanteId(Long estudanteId) {
        this.estudanteId = estudanteId;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public StatusSuspensao getStatusSuspensao() {
        return statusSuspensao;
    }

    public void setStatusSuspensao(StatusSuspensao statusSuspensao) {
        this.statusSuspensao = statusSuspensao;
    }
}
