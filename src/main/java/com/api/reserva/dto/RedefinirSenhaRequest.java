package com.api.reserva.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RedefinirSenhaRequest {
    @NotBlank(message = "O identificador é obrigatório")
    private String identificador;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
}
