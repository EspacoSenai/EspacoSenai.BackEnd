package com.api.reserva.dto;

import com.api.reserva.entity.Ambiente;
import com.api.reserva.enums.Aprovacao;
import com.api.reserva.enums.Disponibilidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class AmbienteDTO {
    private Long id;

    @NotBlank(message = "Nome do ambiente é obrigatório.")
    @Size(max = 100, message = "Nome do ambiente deve possuir no máximo 100 caracteres.")
    private String nome;

    @Size(max = 500, message = "Descrição não deve ultrapassar 500 caracteres.")
    private String descricao;

    @NotNull(message = "Escolha uma disponibilidade.")
    private Disponibilidade disponibilidade;

    @NotNull(message = "Escolha uma aprovação.")
    private Aprovacao aprovacao;

    private Long responsavelId;

    private boolean emUso;

    private boolean recurso;

    private boolean soInternos;

    private Set<Long> recursosIds = new HashSet<>();

    public AmbienteDTO() {
    }

    public AmbienteDTO(String nome, String descricao, Disponibilidade disponibilidade, Aprovacao aprovacao,
                       Long responsavelId, boolean emUso, boolean recurso, boolean soInternos, Set<Long> recursosIds) {
        this.nome = nome;
        this.descricao = descricao;
        this.disponibilidade = disponibilidade;
        this.aprovacao = aprovacao;
        this.responsavelId = responsavelId;
        this.emUso = emUso;
        this.recurso = recurso;
        this.soInternos = soInternos;
        this.recursosIds = recursosIds;
    }

    public AmbienteDTO(Ambiente ambiente) {
        id = ambiente.getId();
        nome = ambiente.getNome();
        descricao = ambiente.getDescricao();
        disponibilidade = ambiente.getDisponibilidade();
        aprovacao = ambiente.getAprovacao();
        emUso = ambiente.isEmUso();
        recurso = ambiente.isRecurso();
        soInternos = ambiente.isSoInternos();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao.trim();
    }

    public Disponibilidade getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Disponibilidade disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public Aprovacao getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(Aprovacao aprovacao) {
        this.aprovacao = aprovacao;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

    public boolean isEmUso() {
        return emUso;
    }

    public void setEmUso(boolean emUso) {
        this.emUso = emUso;
    }

    public boolean isRecurso() {
        return recurso;
    }

    public void setRecurso(boolean recurso) {
        this.recurso = recurso;
    }

    public boolean isSoInternos() {
        return soInternos;
    }

    public void setSoInternos(boolean soInternos) {
        this.soInternos = soInternos;
    }

    public Set<Long> getRecursosIds() {
        return recursosIds;
    }

    public void setRecursosIds(Set<Long> recursosIds) {
        this.recursosIds = recursosIds;
    }
}