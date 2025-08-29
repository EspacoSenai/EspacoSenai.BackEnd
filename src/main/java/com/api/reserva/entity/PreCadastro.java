package com.api.reserva.entity;

import com.api.reserva.dto.PreCadastroDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_pre_cadastro")
public class PreCadastro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nome;


    @Column(unique = true, length = 100, nullable = false)
    private String email;

    public PreCadastro() {
    }

    public PreCadastro(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public PreCadastro(PreCadastroDTO preCadastroDTO) {
        nome = preCadastroDTO.getNome();
        email = preCadastroDTO.getEmail();
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
}
