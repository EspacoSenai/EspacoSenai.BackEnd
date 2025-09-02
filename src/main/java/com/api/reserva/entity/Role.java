package com.api.reserva.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tb_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Values roleNome;

    public Long getId() {
        return id;
    }

    public Values getRoleNome() {
        return roleNome;
    }

    public enum Values {
        ADMIN(1L),
        COORDENADOR(2L),
        PROFESSOR(3L),
        ESTUDANTE(4L);

        Long roleId;

        Values(Long roleId) {
            this.roleId = roleId;
        }
    }
}
