package com.api.reserva.entity;

import com.api.reserva.dto.GradeAulaDTO;
import com.api.reserva.enums.DiaSemana;
import jakarta.persistence.*;

@Entity
public class GradeAula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sala;

    @ManyToOne
    @JoinColumn(name = "id_professor")
    private Usuario professor;

    @ManyToOne
    @JoinColumn(name = "id_disciplina")
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "id_horario")
    private Horario horario;

    @ManyToOne
    @JoinColumn(name = "id_periodo")
    private Periodo periodo;

    @Enumerated(EnumType.STRING)
    private DiaSemana dia;

    public GradeAula(String sala, Usuario professor, Disciplina disciplina,
                     Horario horario, Periodo periodo, DiaSemana dia) {
        this.sala = sala;
        this.professor = professor;
        this.disciplina = disciplina;
        this.horario = horario;
        this.periodo = periodo;
        this.dia = dia;
    }

    public GradeAula(GradeAulaDTO dto, Usuario professor, Disciplina disciplina,
                     Horario horario, Periodo periodo) {
        this.sala = dto.getSala();
        this.professor = professor;
        this.disciplina = disciplina;
        this.horario = horario;
        this.periodo = periodo;
        this.dia = dto.getDia();
    }

    public GradeAula() {

    }

    public Long getId() { return id; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public Usuario getProfessor() { return professor; }
    public void setProfessor(Usuario professor) { this.professor = professor; }

    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina; 
    }

    public Horario getHorario() { return horario; }
    public void setHorario(Horario horario) { this.horario = horario; }

    public Periodo getPeriodo() { return periodo; }
    public void setPeriodo(Periodo periodo) { this.periodo = periodo; }

    public DiaSemana getDia() { return dia; }
    public void setDia(DiaSemana dia) { this.dia = dia; }
}