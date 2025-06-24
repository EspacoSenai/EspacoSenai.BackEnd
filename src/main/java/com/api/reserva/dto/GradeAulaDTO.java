package com.api.reserva.dto;

import com.api.reserva.entity.GradeAula;
import com.api.reserva.enums.DiaSemana;

public class GradeAulaDTO {

    private Long id;
    private String sala;
    private UsuarioDTO professor;  // Aqui é UsuarioDTO
    private HorarioDTO horario;
    private PeriodoDTO periodo;
    private DiaSemana dia;

    public GradeAulaDTO() {}

    public GradeAulaDTO(Long id, String sala, UsuarioDTO professor, HorarioDTO horario, PeriodoDTO periodo, DiaSemana dia) {
        this.id = id;
        this.sala = sala;
        this.professor = professor;
        this.horario = horario;
        this.periodo = periodo;
        this.dia = dia;
    }

    public GradeAulaDTO(GradeAula entity) {
        this.id = entity.getId();
        this.sala = entity.getSala();
        this.professor = new UsuarioDTO(entity.getProfessor()); // usa UsuarioDTO aqui
        this.horario = new HorarioDTO(entity.getHorario());
        this.periodo = new PeriodoDTO(entity.getPeriodo());
        this.dia = entity.getDia();
    }

    public Long getId() { return id; }

    public String getSala() { return sala; }

    public void setSala(String sala) { this.sala = sala; }

    public UsuarioDTO getProfessor() { return professor; }

    public void setProfessor(UsuarioDTO professor) { this.professor = professor; }

    public HorarioDTO getHorario() { return horario; }

    public void setHorario(HorarioDTO horario) { this.horario = horario; }

    public PeriodoDTO getPeriodo() { return periodo; }

    public void setPeriodo(PeriodoDTO periodo) { this.periodo = periodo; }

    public DiaSemana getDia() { return dia; }

    public void setDia(DiaSemana dia) { this.dia = dia; }
}