package com.api.reserva.entity;

import com.api.reserva.dto.TurmaDTO;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_turma")
public class Turma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataTermino;

    @ManyToMany
    @JoinTable(
            name = "tb_estudante_turma",
            joinColumns = @JoinColumn(name = "turma_id"),
            inverseJoinColumns = @JoinColumn(name = "estudante_id")
    )
    private Set<Usuario> estudantes = new HashSet<>();

    public Turma() {
    }

    public Turma(String nome, LocalDate dataInicio, LocalDate dataTermino) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
    }

    public Turma(TurmaDTO turmaDTO) {
        this.nome = turmaDTO.getNome();
        this.dataInicio = turmaDTO.getDataInicio();
        this.dataTermino = turmaDTO.getDataTermino();
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

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(LocalDate dataTermino) {
        this.dataTermino = dataTermino;
    }

    public Set<Usuario> getEstudantes() {
        return estudantes;
    }

    public void setEstudantes(Set<Usuario> estudantes) {
        this.estudantes = estudantes;
    }
}
