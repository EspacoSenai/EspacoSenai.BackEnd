package com.api.reserva.dto;

import com.api.reserva.entity.Turma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public class TurmaDTO {

    private Long id;

    @NotBlank(message = "O nome da turma é obrigatório.")
    @Size(max = 100, message = "O nome da turma deve ter no máximo 100 caracteres.")
    private String nome;

    @NotNull(message = "Escolha um curso para a turma.")
    private Long cursoId;

    @NotNull(message = "Escolha a modalidade da turma.")
    private Turma.Modalidade modalidade;

    @NotNull(message = "A data que turma começa o curso é obrigatória.")
    private LocalDate dataInicio;

    @NotNull(message = "A data que turma termina o curso é obrigatória.")
    private LocalDate dataTermino;

    private String codigoAcesso;

    private Long professorId;

    private Set<Long> estudantesIds;

    public TurmaDTO() {
    }

    public TurmaDTO(Long id, String nome, LocalDate dataInicio, LocalDate dataTermino) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
    }

    public TurmaDTO(Turma turma) {
        this.id = turma.getId();
        this.nome = turma.getNome();
        this.cursoId = turma.getCurso().getId();
        this.modalidade = turma.getModalidade();
        this.dataInicio = turma.getDataInicio();
        this.dataTermino = turma.getDataTermino();
        this.codigoAcesso = turma.getCodigoAcesso();
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

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
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
}
