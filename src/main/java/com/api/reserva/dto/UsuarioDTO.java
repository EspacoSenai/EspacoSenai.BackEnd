package com.api.reserva.dto;

import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioRole;
import com.api.reserva.enums.UsuarioStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioDTO {
    private Long id;
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres.")
    @NotBlank(message = "Nome é obrigatório.")
    private String nome;
    @Size(max = 100, message = "O email deve ter no máximo 100 caracteres.")
    @NotBlank(message = "Email é obrigatório.")
    @Email(message = "Email inválido.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email inválido.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String email;
    @NotBlank(message = "Senha é obrigatória.")
    private String senha;
    @Size(min = 11, max = 11, message = "O telefone deve conter 11 digitos.")
    @Pattern(regexp = "^[1-9]{2}[9]{1}[0-9]{8}$", message = "Telefone inválido. Use apenas números: DDD + 9 dígitos")
    private String telefone;

    private String tag;

    private UsuarioStatus status;

    private UsuarioRole role;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String nome, String email, String senha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public UsuarioDTO(String nome, String email, String senha, String telefone, String tag,
                      UsuarioStatus status, UsuarioRole role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.tag = tag;
        this.status = status;
        this.role = role;
    }

    public UsuarioDTO(Usuario usuario) {
        id = usuario.getId();
        nome = usuario.getNome();
        email = usuario.getEmail();
        senha = usuario.getSenha();
        telefone = usuario.getTelefone();
        status = usuario.getStatus();
        role = usuario.getRole();
        tag = usuario.getTag();
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public UsuarioStatus getStatus() {
        return status;
    }

    public void setStatus(UsuarioStatus status) {
        this.status = status;
    }

    public UsuarioRole getRole() {
        return role;
    }

    public void setRole(UsuarioRole role) {
        this.role = role;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
