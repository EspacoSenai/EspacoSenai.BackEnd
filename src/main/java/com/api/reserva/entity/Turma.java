package com.api.reserva.entity;

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

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidade modalidade;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataTermino;

    @ManyToMany
    @JoinTable(
            name = "tb_turmas_estudantes",
            joinColumns = @JoinColumn(name = "turma_id"),
            inverseJoinColumns = @JoinColumn(name = "estudante_id"))
    private Set<Usuario> estudantes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "tb_turmas_professores",
            joinColumns = @JoinColumn(name = "turma_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id"))
    private Set<Usuario> professores = new HashSet<>();

    public Turma() {
    }

    public Turma(String nome, LocalDate dataInicio, LocalDate dataTermino) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
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

    public Modalidade getModalidade() {
        return modalidade;
    }

    public void setModalidade(Modalidade modalidade) {
        this.modalidade = modalidade;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Set<Usuario> getProfessores() {
        return professores;
    }

    public void setProfessores(Set<Usuario> professores) {
        this.professores = professores;
    }

    public enum Modalidade {
        FIC(1L),
        TECNICO(2L),
        FACULDADE(3L);

        Long modalidadeId;

        Modalidade(Long modalidadeId) {
            this.modalidadeId = modalidadeId;
        }
    }
}
