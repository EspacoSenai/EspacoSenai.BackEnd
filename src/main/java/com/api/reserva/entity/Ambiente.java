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

    @Column(unique = true, nullable = false, length = 100)
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

    private boolean emUso;

    @OneToMany(mappedBy = "ambiente", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Catalogo> catalogos = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "tb_ambiente_responsavel",
            joinColumns = @JoinColumn(name = "ambiente_id"),
            inverseJoinColumns = @JoinColumn(name = "responsavel_id")
    )
    private Set<Usuario> responsaveis = new HashSet<>();


    public Ambiente() {
    }


    public Ambiente(AmbienteDTO ambienteDTO) {
        nome = ambienteDTO.getNome();
        descricao = ambienteDTO.getDescricao();
        disponibilidade = ambienteDTO.getDisponibilidade();
        aprovacao = ambienteDTO.getAprovacao();
        qtdPessoas = ambienteDTO.getQtdPessoas();
        emUso = ambienteDTO.isEmUso();
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

    public boolean isEmUso() {
        return emUso;
    }

    public void setEmUso(boolean emUso) {
        this.emUso = emUso;
    }

    public Set<Catalogo> getCatalogos() {
        return catalogos;
    }

    public void setCatalogos(Set<Catalogo> catalogos) {
        this.catalogos = catalogos;
    }
}
