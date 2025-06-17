package com.api.reserva.entity;

import com.api.reserva.enums.DiaSemana;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_grade_aula")
public class GradeAula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String sala;

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false)
    private Usuario professor;

    @ManyToOne
    @JoinColumn(name = "id_horario", nullable = false)
    private Horario horario;

    @ManyToOne
    @JoinColumn(name = "id_periodo", nullable = false)
    private Periodo periodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana dia;

    // 🔸 Construtor padrão
    public GradeAula() {
    }

    // 🔸 Construtor completo
    public GradeAula(String sala, Usuario professor, Horario horario, Periodo periodo, DiaSemana dia) {
        this.sala = sala;
        this.professor = professor;
        this.horario = horario;
        this.periodo = periodo;
        this.dia = dia;
    }

    // 🔸 Getters e Setters
    public Long getId() {
        return id;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public Usuario getProfessor() {
        return professor;
    }

    public void setProfessor(Usuario professor) {
        this.professor = professor;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }
}
