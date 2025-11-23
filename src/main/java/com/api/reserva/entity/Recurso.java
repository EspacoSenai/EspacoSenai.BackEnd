package com.api.reserva.entity;

import com.api.reserva.enums.Disponibilidade;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_ambientes_associados")
public class Recurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Ambiente ambiente;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private Disponibilidade disponibilidade;

    private boolean emUso;

    public Recurso() {
    }

    public Recurso(Ambiente ambiente, String nome, String descricao, Disponibilidade disponibilidade) {
        this.ambiente = ambiente;
        this.nome = nome;
        this.descricao = descricao;
        this.disponibilidade = disponibilidade;
        this.emUso = false;
    }

    public Long getId() {
        return id;
    }

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
        this.ambiente = ambiente;
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
