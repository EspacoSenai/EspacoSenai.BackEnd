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

public class AmbienteDTO {
    private Long id;

    @NotBlank(message = "Nome do ambiente é obrigatório.")
    @Size(min = 5, max = 50, message = "Nome do ambiente deve ter entre 5 e 50 caracteres.")
    private String nome;

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres.")
    private String descricao;

    @NotNull(message = "Disponibilidade é obrigatória.")
    private Disponibilidade disponibilidade;

    @NotNull(message = "Tipo de aprovação é obrigatório.")
    private Aprovacao aprovacao;

    private Set<CategoriaReferenciaDTO> categorias = new HashSet<>();

    public AmbienteDTO() {
    }

    public AmbienteDTO(String nome, String descricao, Disponibilidade disponibilidade, Aprovacao aprovacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.disponibilidade = disponibilidade;
        this.aprovacao = aprovacao;
    }

    public AmbienteDTO(Ambiente ambiente) {
        id = ambiente.getId();
        nome = ambiente.getNome();
        descricao = ambiente.getDescricao();
        disponibilidade = ambiente.getDisponibilidade();
        aprovacao = ambiente.getAprovacao();
        categorias = ambiente.getCategorias()
                .stream()
                .map(CategoriaReferenciaDTO::new)
                .collect(Collectors.toSet());

//        categorias = ambiente.getCategorias().stream()
//                .map(Categoria::getId)
//                .collect(Collectors.toSet());
    }


//    private String validarNome(String nome) {
//        if (nome == null) {
//            throw new DadoInvalidoException("O nome do ambiente não pode ser nulo.");
//        }
//
//        nome = nome.trim();
//
//        if (nome.isBlank()) {
//            throw new DadoInvalidoException("O nome não pode estar em branco.");
//        } else if (nome.length() < 5 || nome.length() > 50) {
//            throw new DadoInvalidoException("O nome do ambiente deve estar entre 5 e 50 caracteres.");
//        }
//        return nome;
//    }
//
//    private String validarDescricao(String descricao){
//        if(descricao != null || !descricao.isBlank()) {
//            descricao = descricao.trim();
//            if (descricao.length() > 500) {
//                throw new DadoInvalidoException("A descrição deve ter no máximo 500 caracteres.");
//            }
//        }
//        return descricao;
//    }

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

    public Set<CategoriaReferenciaDTO> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<CategoriaReferenciaDTO> categorias) {
        this.categorias = categorias;
    }
}