package com.api.reserva.dto;

import com.api.reserva.entity.Turma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class TurmaDTO {

    private Long id;

    @NotBlank(message = "O nome da turma é obrigatório.")
    @Size(max = 100, message = "O nome da turma deve ter no máximo 100 caracteres.")
    private String nome;

    @NotNull(message = "Escolha a modalidade da turma.")
    private Turma.Modalidade modalidade;

    @NotBlank(message = "O nome do curso é obrigatório.")
    private String curso;

    @NotNull(message = "A data que turma começa o curso é obrigatória.")
    private LocalDate dataInicio;

    @NotNull(message = "A data que turma termina o curso é obrigatória.")
    private LocalDate dataTermino;

    private String codigoAcesso;

    @NotNull(message = "A capacidade máxima da turma é obrigatória.")
    @Positive(message = "A capacidade máxima da turma deve ser um número positivo.")
    private Integer capacidade;

    // professorId não deve ser obrigatório no DTO porque o serviço pode inferir pelo auth
    private Long professorId;

    // inicializar para evitar NPEs ao consultar tamanho em serviços
    private Set<Long> estudantesIds = new HashSet<>();

    public TurmaDTO() {
    }

    public TurmaDTO(String nome, Turma.Modalidade modalidade, String curso, LocalDate dataInicio, LocalDate dataTermino,
                    String codigoAcesso, Long professorId, Set<Long> estudantesIds, Integer capacidade) {
        this.nome = nome;
        this.modalidade = modalidade;
        this.curso = curso;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.codigoAcesso = codigoAcesso;
        this.capacidade = capacidade;
        this.professorId = professorId;
        if (estudantesIds != null) {
            this.estudantesIds = estudantesIds;
        }
    }

    public TurmaDTO(Turma turma) {
        this.id = turma.getId();
        this.nome = turma.getNome();
        this.curso = turma.getCurso();
        this.modalidade = turma.getModalidade();
        this.dataInicio = turma.getDataInicio();
        this.dataTermino = turma.getDataTermino();
        this.capacidade = turma.getCapacidade();
        this.codigoAcesso = turma.getCodigoAcesso();
        if (turma.getProfessor() != null) {
            this.professorId = turma.getProfessor().getId();
        }
        // popular estudantesIds a partir da entidade, se necessário (mantemos apenas ids)
        if (turma.getEstudantes() != null && !turma.getEstudantes().isEmpty()) {
            turma.getEstudantes().forEach(u -> this.estudantesIds.add(u.getId()));
        }
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

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Turma.Modalidade getModalidade() {
        return modalidade;
    }

    public void setModalidade(Turma.Modalidade modalidade) {
        this.modalidade = modalidade;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    public Set<Long> getEstudantesIds() {
        return estudantesIds;
    }

    public void setEstudantesIds(Set<Long> estudantesIds) {
        this.estudantesIds = estudantesIds;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public String getCurso() {
        return curso;
    }
}
