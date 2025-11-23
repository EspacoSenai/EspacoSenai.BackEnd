package com.api.reserva.dto;

import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Recurso;
import com.api.reserva.enums.Aprovacao;
import com.api.reserva.enums.Disponibilidade;

import java.util.Set;

public class AmbienteReferenciaDTO {
    private Long id;
    private String nome;
    private Disponibilidade disponibilidade;
    private Aprovacao aprovacao;
    private Set<CatalogoReferenciaDTO> catalogos;
    private UsuarioReferenciaDTO responsavel;
    private Set<Recurso> recursos;

    public AmbienteReferenciaDTO() {
    }

    public AmbienteReferenciaDTO(Ambiente ambiente) {
        id = ambiente.getId();
        nome = ambiente.getNome();
        disponibilidade = ambiente.getDisponibilidade();
        aprovacao = ambiente.getAprovacao();
        responsavel = ambiente.getResponsavel() != null ? new UsuarioReferenciaDTO(ambiente.getResponsavel()) : null;
        recursos = ambiente.getRecursos();
        catalogos = ambiente.getCatalogos().stream().map(CatalogoReferenciaDTO::new).collect(java.util.stream.Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public UsuarioReferenciaDTO getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioReferenciaDTO responsavel) {
        this.responsavel = responsavel;
    }

    public Set<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(Set<Recurso> recursos) {
        this.recursos = recursos;
    }

    public Set<CatalogoReferenciaDTO> getCatalogos() {
        return catalogos;
    }

    public void setCatalogos(Set<CatalogoReferenciaDTO> catalogos) {
        this.catalogos = catalogos;
    }
}
