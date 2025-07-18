package com.api.reserva.dto;

import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Categoria;

import java.util.Set;
import java.util.stream.Collectors;

public class CategoriaReferenciaDTO {
    private Long id;
    private String nome;
    private Set<Long> ambientes;

    public CategoriaReferenciaDTO() {
    }

    public CategoriaReferenciaDTO(Categoria categoria) {
        id = categoria.getId();
        nome = categoria.getNome();
        ambientes = categoria.getAmbientes()
                .stream().map(Ambiente::getId)
                .collect(Collectors.toSet());
    }
//    public CategoriaReferenciaDTO(Categoria categoria) {
//        id = categoria.getId();
//        nome = categoria.getNome();
//        ambientes = categoria.getAmbientes()
//                .stream().map(Ambiente::getId)
//                .collect(Collectors.toSet());
//    }

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

    public Set<Long> getAmbientes() {
        return ambientes;
    }

    public void setAmbientes(Set<Long> ambientes) {
        this.ambientes = ambientes;
    }
}
