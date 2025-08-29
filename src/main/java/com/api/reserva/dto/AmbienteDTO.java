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
    @Size(min = 5, max = 100, message = "Nome do ambiente deve possuir entre 5 e 100 caracteres.")
    private String nome;

    @Size(max = 500, message = "Descrição não deve ultrapassar 500 caracteres.")
    private String descricao;

    @NotNull(message = "Escolha uma disponibilidade.")
    private Disponibilidade disponibilidade;

    @NotNull(message = "Escolha uma aprovação.")
    private Aprovacao aprovacao;

//    private Set<CategoriaReferenciaDTO> categorias = new HashSet<>();
    private Set<Long> categoriasIds = new HashSet<>();

    private Set<Long> responsaveisIds = new HashSet<>();

    private Integer qtdPessoas;

    public AmbienteDTO() {
    }

    public AmbienteDTO(String nome, String descricao, Disponibilidade disponibilidade, Aprovacao aprovacao,
                       Set<Long> categoriasIds, Set<Long> responsaveisIds, Integer qtdPessoas) {
        this.nome = nome;
        this.descricao = descricao;
        this.disponibilidade = disponibilidade;
        this.aprovacao = aprovacao;
        this.categoriasIds = categoriasIds;
        this.responsaveisIds = responsaveisIds;
        this.qtdPessoas = qtdPessoas;
    }

    public AmbienteDTO(Ambiente ambiente) {
        id = ambiente.getId();
        nome = ambiente.getNome();
        descricao = ambiente.getDescricao();
        disponibilidade = ambiente.getDisponibilidade();
        aprovacao = ambiente.getAprovacao();
        qtdPessoas = ambiente.getQtdPessoas();
//        categoriasIds = ambiente.getCategorias().stream()
//                .map(Categoria::getId)
//                .collect(Collectors.toSet());
////        categorias = ambiente.getCategorias()
////                .stream()
////                .map(CategoriaReferenciaDTO::new)
////                .collect(Collectors.toSet());
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

    public Set<Long> getCategoriasIds() {
        return categoriasIds;
    }

    public void setCategoriasIds(Set<Long> categoriasIds) {
        this.categoriasIds = categoriasIds;
    }

//    public Set<CategoriaReferenciaDTO> getCategorias() {
//        return categorias;
//    }
//
//    public void setCategorias(Set<CategoriaReferenciaDTO> categorias) {
//        this.categorias = categorias;
//    }


    public Set<Long> getResponsaveisIds() {
        return responsaveisIds;
    }

    public void setResponsaveisIds(Set<Long> responsaveisIds) {
        this.responsaveisIds = responsaveisIds;
    }

    public Integer getQtdPessoas() {
        return qtdPessoas;
    }
}