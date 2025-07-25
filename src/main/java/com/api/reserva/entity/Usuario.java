package com.api.reserva.entity;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.enums.UsuarioRole;
import com.api.reserva.enums.UsuarioStatus;
import jakarta.persistence.*;
import org.hibernate.FetchMode;
import org.hibernate.annotations.Fetch;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    private String senha;

    @Column(unique = true, length = 11)
    private String telefone;

    @Column(unique = true, length = 8)
    private String tag;

    @Enumerated(EnumType.STRING)
    private UsuarioStatus status;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Usuario(String nome, String email, String senha, String telefone, String tag,
                   UsuarioStatus status, UsuarioRole role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.tag = tag;
        this.status = status;
        this.role = role;
    }

    public Usuario(UsuarioDTO usuarioDTO) {
        nome = usuarioDTO.getNome();
        email = usuarioDTO.getEmail();
        senha = usuarioDTO.getSenha();
        telefone = usuarioDTO.getTelefone();
        tag = usuarioDTO.getTag();
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
