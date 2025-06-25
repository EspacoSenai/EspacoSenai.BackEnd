package com.api.reserva.entity;

import com.api.reserva.dto.DisciplinaDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_disciplina")
public class Disciplina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String disciplina;

    public Disciplina(Long id, String disciplina) {
        this.id = id;
        this.disciplina = disciplina;
    }

    public Disciplina(DisciplinaDTO dto) {
        if (dto != null) {
            this.id = dto.getId();
            this.disciplina = dto.getDisciplina();
        }
    }

    public Disciplina(){}

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
