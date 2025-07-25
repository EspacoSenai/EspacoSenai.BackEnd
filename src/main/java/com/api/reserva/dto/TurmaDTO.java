package com.api.reserva.dto;

import com.api.reserva.entity.Turma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TurmaDTO {

    private Long id;

    @NotBlank(message = "O nome da turma é obrigatório.")
    @Size(max = 100, message = "O nome da turma deve ter no máximo 100 caracteres.")
    private String nome;

    @NotNull(message = "A data que turma começa o curso é obrigatória.")
    private LocalDate dataInicio;

    @NotNull(message = "A data que turma termina o curso é obrigatória.")
    private LocalDate dataTermino;

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
        this.dataInicio = turma.getDataInicio();
        this.dataTermino = turma.getDataTermino();
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
}
