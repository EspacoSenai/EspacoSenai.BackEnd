package com.api.reserva.dto;

import com.api.reserva.entity.Disciplina;

public class DisciplinaDTO {

    private Long id;
    private String disciplina;

    public DisciplinaDTO() {
    }

    public DisciplinaDTO(Long id, String disciplina) {
        this.id = id;
        this.disciplina = disciplina;
    }

    // Construtor que preenche o DTO a partir da entidade
    public DisciplinaDTO(Disciplina entidade) {
        if (entidade != null) {
            this.id = entidade.getId();
            this.disciplina = entidade.getDisciplina();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }
}
