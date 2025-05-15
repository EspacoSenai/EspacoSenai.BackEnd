package com.api.reserva.entity;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.enums.UsuarioGenero;
import com.api.reserva.enums.UsuarioRole;
import com.api.reserva.enums.UsuarioStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 100, nullable = false)
    private String email;

    private String senha;

    @Column(length = 11)
    private String telefone;

    @Enumerated(EnumType.STRING)
    private UsuarioStatus status;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String telefone,
                   UsuarioStatus status, UsuarioRole role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.status = status;
        this.role = role;
    }

    public Usuario(UsuarioDTO usuarioDTO) {
        id = usuarioDTO.getId();
        nome = usuarioDTO.getNome();
        email = usuarioDTO.getEmail();
        senha = usuarioDTO.getSenha();
        telefone = usuarioDTO.getTelefone();
        status = usuarioDTO.getStatus();
        role = usuarioDTO.getRole();
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
}
