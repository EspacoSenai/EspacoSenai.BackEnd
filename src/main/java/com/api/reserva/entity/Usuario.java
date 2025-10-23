package com.api.reserva.entity;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.enums.UsuarioStatus;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Random;
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

    @Column(unique = true, length = 5)
    private String tag;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UsuarioStatus status;

    @OneToMany(mappedBy = "usuario")
    private Set<Notificacao> notificacoes = new HashSet<>();

    @OneToMany(mappedBy = "convidado", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservaConvidados> convites = new HashSet<>();


    @Column(nullable = false)
    @ManyToMany
    @JoinTable(
            name = "tb_usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String tag,
                   UsuarioStatus status) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tag = tag;
        this.status = status;
    }

    public Usuario(UsuarioDTO usuarioDTO) {
        nome = usuarioDTO.getNome();
        email = usuarioDTO.getEmail();
        senha = usuarioDTO.getSenha();
        tag = usuarioDTO.getTag();
        status = usuarioDTO.getStatus();
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;

    }

    public void gerarTag() {
        Random random = new Random();
        Integer nmrTag = random.nextInt(9999) + 1;
//
////        if (this.roles == UsuarioRole.ESTUDANTE) {
////            this.tag = String.format("ESE%05d", nmrTag);
////        } else if (this.role == UsuarioRole.COORDENADOR) {
////            this.tag = String.format("ESC%05d", nmrTag);
////        } else if (this.role == UsuarioRole.ADMIN) {
////            this.tag = String.format("ESA%05d", nmrTag);
////        } else {
////            throw new TagCriacaoException();
////        }
        this.tag = String.format("%07d", nmrTag);
    }

    public boolean isLoginValid(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.senha);
    }

    public Set<ReservaConvidados> getConvites() {
        return convites;
    }

    public void setConvites(Set<ReservaConvidados> convites) {
        this.convites = convites;
    }
}