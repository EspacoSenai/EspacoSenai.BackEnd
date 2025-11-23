package com.api.reserva.dto;

import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioStatus;

import java.util.Set;

public class UsuarioReferenciaDTO {
    private Long id;

    private String nome;

    private String email;

    private String senha;

    private UsuarioStatus status;


    private Set<NotificacaoDTO> notificacoes;

    private Set<Role> roles;

//    private Set<ReservaReferenciaDTO>  hostReservas;
//
//    private Set<ReservaReferenciaDTO> membroReservas;


    public UsuarioReferenciaDTO() {
    }

    public UsuarioReferenciaDTO(Long id, String nome, String email, String senha, UsuarioStatus status, Set<Role> roles,
                                Set<NotificacaoDTO> notificacoes, Set<ReservaReferenciaDTO> hostReservas, Set<ReservaReferenciaDTO> membroReservas) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.status = status;
        this.notificacoes = notificacoes;
        this.roles = roles;
//        this.hostReservas = hostReservas;
//        this.membroReservas = membroReservas;
    }

    public UsuarioReferenciaDTO(Usuario usuario) {
        id = usuario.getId();
        nome = usuario.getNome();
        email = usuario.getEmail();
        senha = usuario.getSenha();
//        tag = usuario.getTag();
        status = usuario.getStatus();
        notificacoes = usuario.getNotificacoes().stream().map(NotificacaoDTO::new).collect(java.util.stream.Collectors.toSet());
        roles = usuario.getRoles();
//        hostReservas = usuario.getHostReservas().stream().map(ReservaReferenciaDTO::new).collect(java.util.stream.Collectors.toSet());
//        membroReservas = usuario.getMembroReservas().stream().map(ReservaReferenciaDTO::new).collect(java.util.stream.Collectors.toSet());
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

    public Set<NotificacaoDTO> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(Set<NotificacaoDTO> notificacoes) {
        this.notificacoes = notificacoes;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
//
//    public Set<ReservaReferenciaDTO> getHostReservas() {
//        return hostReservas;
//    }
//
//    public void setHostReservas(Set<ReservaReferenciaDTO> hostReservas) {
//        this.hostReservas = hostReservas;
//    }
//
//    public Set<ReservaReferenciaDTO> getMembroReservas() {
//        return membroReservas;
//    }
//
//    public void setMembroReservas(Set<ReservaReferenciaDTO> membroReservas) {
//        this.membroReservas = membroReservas;
//    }
}
