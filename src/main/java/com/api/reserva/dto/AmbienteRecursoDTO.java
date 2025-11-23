package com.api.reserva.dto;

import com.api.reserva.entity.Recurso;
import com.api.reserva.enums.Disponibilidade;

public class AmbienteRecursoDTO {
    private Long id;
    private Long ambienteId;
    private String nome;
    private String descricao;
    private Disponibilidade disponibilidade;
    private boolean emUso;
    public AmbienteRecursoDTO() {
    }

    public AmbienteRecursoDTO(Long ambienteId, String nome, String descricao, Disponibilidade disponibilidade, boolean emUso) {
        this.ambienteId = ambienteId;
        this.nome = nome;
        this.descricao = descricao;
        this.disponibilidade = disponibilidade;
        this.emUso = emUso;
    }

    public AmbienteRecursoDTO(Recurso recurso) {
        this.id = recurso.getId();
        this.ambienteId = recurso.getAmbiente().getId();
        this.nome = recurso.getNome();
        this.descricao = recurso.getDescricao();
        this.disponibilidade = recurso.getDisponibilidade();
        this.emUso = recurso.isEmUso();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmbienteId() {
        return ambienteId;
    }

    public void setAmbienteId(Long ambienteId) {
        this.ambienteId = ambienteId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Disponibilidade getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Disponibilidade disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public boolean isEmUso() {
        return emUso;
    }

    public void setEmUso(boolean emUso) {
        this.emUso = emUso;
    }
}
