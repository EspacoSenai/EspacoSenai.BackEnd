package com.api.reserva.dto;

import com.api.reserva.entity.Turma;

import java.time.LocalDate;
import java.util.Set;

public class TurmaReferenciaDTO {
    private Long id;

    private String nome;

    private String curso;

    private Turma.Modalidade modalidade;

    private LocalDate dataInicio;

    private LocalDate dataTermino;

    private String codigoAcesso;

    private Integer capacidade;

    private UsuarioReferenciaDTO professor;

    private Set<UsuarioReferenciaDTO> estudantesIds;

    public TurmaReferenciaDTO() {
    }

    public TurmaReferenciaDTO(Turma turma) {
        this.id = turma.getId();
        this.nome = turma.getNome();
        this.curso = turma.getCurso();
        this.modalidade = turma.getModalidade();
        this.dataInicio = turma.getDataInicio();
        this.dataTermino = turma.getDataTermino();
        this.codigoAcesso = turma.getCodigoAcesso();
        this.capacidade = turma.getCapacidade();
        this.professor = new UsuarioReferenciaDTO(turma.getProfessor());
        this.estudantesIds = turma.getEstudantes().stream().map(UsuarioReferenciaDTO::new).collect(java.util.stream.Collectors.toSet());
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

    public String getCurso() {
        return curso;
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

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public UsuarioReferenciaDTO getProfessor() {
        return professor;
    }

    public void setProfessor(UsuarioReferenciaDTO professor) {
        this.professor = professor;
    }

    public Set<UsuarioReferenciaDTO> getEstudantesIds() {
        return estudantesIds;
    }

    public void setEstudantesIds(Set<UsuarioReferenciaDTO> estudantesIds) {
        this.estudantesIds = estudantesIds;
    }
}
