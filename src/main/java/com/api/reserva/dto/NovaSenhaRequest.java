package com.api.reserva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NovaSenhaRequest {
    @Size(min = 8, max = 15, message = "A senha deve possuir entre 8 e 15 caracteres.")
    @NotBlank(message = "A senha não pode ser nula.")
    private String novaSenha;

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
