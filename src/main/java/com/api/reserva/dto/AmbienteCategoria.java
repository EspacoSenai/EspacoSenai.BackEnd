package com.api.reserva.dto;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AmbienteCategoria {
    public Set<Long> idsCategorias;

    public AmbienteCategoria() {
        
    }

    public Set<Long> getIdsCategorias() {
        return idsCategorias;
    }

    public void setIdsCategorias(Set<Long> idsCategorias) {
        this.idsCategorias = idsCategorias;
    }
}
