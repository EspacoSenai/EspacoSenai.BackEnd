package com.api.reserva.dto;

import com.api.reserva.entity.PreCadastro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PreCadastroDTO {

    private Long id;

    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "Email é obrigatório.")
    @Size(max = 100, message = "O email deve ter no máximo 100 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email inválido.")
    private String email;

    private boolean seCadastrou;

    public PreCadastroDTO() {
    }

    public PreCadastroDTO(String email,  boolean seCadastrou) {
        this.id = id;
        this.email = email;
        this.seCadastrou = seCadastrou;
    }

    public PreCadastroDTO(PreCadastro preCadastro) {
        id = preCadastro.getId();
        nome = preCadastro.getNome();
        email = preCadastro.getEmail();
        seCadastrou =  preCadastro.isSeCadastrou();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSeCadastrou() {
        return seCadastrou;
    }
}
