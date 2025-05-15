package com.api.reserva.entity;

import com.api.reserva.enums.DiaSemana;
import jakarta.persistence.*;

@Entity
@Table(name = "grade")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idAmbiente;
    private Long idPeriodo;
    private Long idHorario;
    private DiaSemana diaSemana;

}
