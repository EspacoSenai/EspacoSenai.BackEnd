package com.api.reserva.entity;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.enums.UsuarioStatus;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    private String senha;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UsuarioStatus status;

    @OneToMany(mappedBy = "usuario")
    private Set<Notificacao> notificacoes = new HashSet<>();

    @Column(nullable = false)
    @ManyToMany
    @JoinTable(
            name = "tb_usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private Set<Reserva> hostReservas = new HashSet<>();

    @ManyToMany(mappedBy = "membros", fetch = FetchType.LAZY)
    private Set<Reserva> membroReservas = new HashSet<>();

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha,
                   UsuarioStatus status) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.status = status;
    }

    public Usuario(UsuarioDTO usuarioDTO) {
        nome = usuarioDTO.getNome();
        email = usuarioDTO.getEmail();
        senha = usuarioDTO.getSenha();
        status = usuarioDTO.getStatus();
    }

//    public void gerarTag() {
//        Random random = new Random();
//        Integer nmrTag = random.nextInt(9999) + 1;
////
//////        if (this.roles == UsuarioRole.ESTUDANTE) {
//////            this.tag = String.format("ESE%05d", nmrTag);
//////        } else if (this.role == UsuarioRole.COORDENADOR) {
//////            this.tag = String.format("ESC%05d", nmrTag);
//////        } else if (this.role == UsuarioRole.ADMIN) {
//////            this.tag = String.format("ESA%05d", nmrTag);
//////        } else {
//////            throw new TagCriacaoException();
//////        }
//        this.tag = String.format("%07d", nmrTag);
//    }

    public boolean isLoginValid(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.senha);
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

    public Set<Notificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(Set<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Reserva> getHostReservas() {
        return hostReservas;
    }

    public void setHostReservas(Set<Reserva> hostReservas) {
        this.hostReservas = hostReservas;
    }

    public Set<Reserva> getMembroReservas() {
        return membroReservas;
    }

    public void setMembroReservas(Set<Reserva> membroReservas) {
        this.membroReservas = membroReservas;
    }
}