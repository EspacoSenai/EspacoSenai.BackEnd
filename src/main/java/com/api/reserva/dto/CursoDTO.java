package com.api.reserva.dto;

import com.api.reserva.entity.Curso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for {@link com.api.reserva.entity.Curso}
 */
public class CursoDTO {
    private Long id;

    @NotBlank
    @Size(max = 100, message = "O nome do curso deve ter no máximo 100 caracteres.")
    private String nome;

    @Size(max = 500, message = "A descrição do curso deve ter no máximo 500 caracteres.")
    private String descricao;

    public CursoDTO() {
    }

    public CursoDTO(Long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public CursoDTO(Curso curso) {
        this.id = curso.getId();
        this.nome = curso.getNome();
        this.descricao = curso.getDescricao();
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
}