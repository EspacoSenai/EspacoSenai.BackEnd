package com.api.reserva.entity;

import com.api.reserva.dto.CategoriaDTO;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String nome;

    @ManyToMany(mappedBy = "categorias")
    private Set<Ambiente> ambientes = new HashSet<>();

    public Categoria() {
    }

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria(CategoriaDTO categoriaDTO) {
        id = categoriaDTO.getId();
        nome = categoriaDTO.getNome();
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

    public Set<Ambiente> getAmbientes() {
        return ambientes;
    }

    public void setAmbientes(Set<Ambiente> ambientes) {
        this.ambientes = ambientes;
    }
}
