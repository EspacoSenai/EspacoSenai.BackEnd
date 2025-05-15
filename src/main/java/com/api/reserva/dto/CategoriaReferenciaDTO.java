package com.api.reserva.dto;

import com.api.reserva.entity.Categoria;

public class CategoriaReferenciaDTO {
    private Long id;
    private String nome;

    public CategoriaReferenciaDTO() {
    }

    public CategoriaReferenciaDTO(Categoria categoria) {
        id = categoria.getId();
        nome = categoria.getNome();
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
}
