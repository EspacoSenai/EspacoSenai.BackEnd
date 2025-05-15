package com.api.reserva.dto;

import com.api.reserva.entity.Ambiente;

public class AmbienteReferenciaDTO {
    private Long id;
    private String nome;

    public AmbienteReferenciaDTO() {
    }

    public AmbienteReferenciaDTO(Ambiente ambiente) {
        id = ambiente.getId();
        nome = ambiente.getNome();
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
