package com.api.reserva.dto;

import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class UsuarioDTO {

    private Long id;

    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    @NotBlank(message = "Nome é obrigatório.")
    private String nome;

    @NotBlank(message = "Email é obrigatório.")
    @Size(max = 100, message = "O email deve ter no máximo 100 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email inválido.")
    private String email;

    @NotBlank(message = "Senha é obrigatória.")
    @Size(min = 8, max = 15,  message = "A senha deve possuir entre 8 e 15 caracteres.")
    private String senha;

    private String tag;

    private UsuarioStatus status;

    private Set<Long> rolesIds = new HashSet<>(); ;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.tag = usuario.getTag();
        this.status = usuario.getStatus();
        this.rolesIds = usuario.getRoles().stream().map(Role::getId).collect(java.util.stream.Collectors.toSet());
    }

    public UsuarioDTO(String nome, String email, String senha, String tag,
                      UsuarioStatus status, Set<Long> roles) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tag = tag;
        this.status = status;
        this.rolesIds = roles;
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

    public UsuarioStatus getStatus() {
        return status;
    }

    public void setStatus(UsuarioStatus status) {
        this.status = status;
    }

    public Set<Long> getRolesIds() {
        return rolesIds;
    }

    public void setRolesIds(Set<Long> rolesIds) {
        this.rolesIds = rolesIds;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
