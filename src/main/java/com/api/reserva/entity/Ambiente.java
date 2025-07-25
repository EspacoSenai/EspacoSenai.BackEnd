package com.api.reserva.entity;

import com.api.reserva.dto.AmbienteDTO;
import com.api.reserva.enums.Aprovacao;
import com.api.reserva.enums.Disponibilidade;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_ambiente")
public class Ambiente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false,  length = 50)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Disponibilidade disponibilidade;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Aprovacao aprovacao;

    @Column(nullable = false)
    private Integer qtdPessoas;

    // Mapeamento muitos para muitos entre Ambiente e Categoria
    @ManyToMany
    @JoinTable(
            name = "tb_ambiente_categoria",
            joinColumns = @JoinColumn(name = "id_ambiente"),
            inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    private Set<Categoria> categorias = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "tb_ambiente_responsavel",
            joinColumns = @JoinColumn(name = "ambiente_id"),
            inverseJoinColumns = @JoinColumn(name = "responsavel_id")
    )
    private Set<Usuario> responsaveis = new HashSet<>();

    public Ambiente() {
    }

    public Ambiente(String nome, String descricao, Disponibilidade disponibilidade, Aprovacao aprovacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.disponibilidade = disponibilidade;
        this.aprovacao = aprovacao;
    }

    public Ambiente(AmbienteDTO ambienteDTO) {
        id = ambienteDTO.getId();
        nome = ambienteDTO.getNome();
        descricao = ambienteDTO.getDescricao();
        disponibilidade = ambienteDTO.getDisponibilidade();
        aprovacao = ambienteDTO.getAprovacao();
    }

    public Long getId() {
        return id;
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

    public Aprovacao getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(Aprovacao aprovacao) {
        this.aprovacao = aprovacao;
    }

    public Set<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Set<Usuario> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(Set<Usuario> responsaveis) {
        this.responsaveis = responsaveis;
    }

    public Integer getQtdPessoas() {
        return qtdPessoas;
    }

    public void setQtdPessoas(Integer qtdPessoas) {
        this.qtdPessoas = qtdPessoas;
    }
}
