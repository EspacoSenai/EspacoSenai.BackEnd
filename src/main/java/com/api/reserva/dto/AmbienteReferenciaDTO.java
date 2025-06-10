package com.api.reserva.dto;

import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Categoria;
import com.api.reserva.enums.Aprovacao;
import com.api.reserva.enums.Disponibilidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AmbienteReferenciaDTO {
    private Long id;
    private String nome;
    private Disponibilidade disponibilidade;
    private Aprovacao aprovacao;

    private Set<Long> categorias;

    public AmbienteReferenciaDTO() {
    }

    public AmbienteReferenciaDTO(Ambiente ambiente) {
        id = ambiente.getId();
        nome = ambiente.getNome();
        disponibilidade = ambiente.getDisponibilidade();
        aprovacao = ambiente.getAprovacao();
        categorias = ambiente.getCategorias()
                .stream()
                .map(Categoria::getId)
                .collect(Collectors.toSet());
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

//    public Set<CategoriaReferenciaDTO> getCategorias() {
//        return categorias;
//    }
//
//    public void setCategorias(Set<CategoriaReferenciaDTO> categorias) {
//        this.categorias = categorias;
//    }

    public Set<Long> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<Long> categorias) {
        this.categorias = categorias;
    }
}
