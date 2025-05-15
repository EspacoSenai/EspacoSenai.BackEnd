package com.api.reserva.dto;

import com.api.reserva.entity.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoriaDTO {
    private Long id;

    @NotBlank(message = "Nome da categoria é obrigatório.")
    @Size(min = 3, max = 20, message = "Nome da categoria deve ter entre 3 e 20 caracteres.")
    private String nome;

    private Set<AmbienteReferenciaDTO> ambientes = new HashSet<>();

    public CategoriaDTO() {
    }

    public CategoriaDTO(String nome) {
        this.nome = nome;
    }

    public CategoriaDTO(Categoria categoria) {
        id = categoria.getId();
        nome = categoria.getNome();
        ambientes = categoria.getAmbientes()
                .stream()
                .map(AmbienteReferenciaDTO::new)
                .collect(Collectors.toSet());
//        ambientes = categoria.getAmbientes().stream()
//                .map(Ambiente::getId)
//                .collect(Collectors.toSet());
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

    public Set<AmbienteReferenciaDTO> getAmbientes() {
        return ambientes;
    }

    public void setAmbientes(Set<AmbienteReferenciaDTO> ambientes) {
        this.ambientes = ambientes;
    }
}
