package com.api.reserva.dto;

import com.api.reserva.entity.GradeAula;
import com.api.reserva.enums.DiaSemana;

public class GradeAulaDTO {

    private Long id;
    private String sala;
    private Long idProfessor;
    private Long idHorario;
    private Long idPeriodo;
    private DiaSemana dia;

    public GradeAulaDTO() {}

    public GradeAulaDTO(Long id, String sala, Long idProfessor, Long idHorario, Long idPeriodo, DiaSemana dia) {
        this.id = id;
        this.sala = sala;
        this.idProfessor = idProfessor;
        this.idHorario = idHorario;
        this.idPeriodo = idPeriodo;
        this.dia = dia;
    }

    public GradeAulaDTO(GradeAula entity) {
        this.id = entity.getId();
        this.sala = entity.getSala();
        this.idProfessor = entity.getProfessor().getId();
        this.idHorario = entity.getHorario().getId();
        this.idPeriodo = entity.getPeriodo().getId();
        this.dia = entity.getDia();
    }

    public Long getId() {return id;}

    public String getSala() {return sala;}

    public void setSala(String sala) {this.sala = sala;}

    public Long getIdProfessor() {return idProfessor;}

    public void setIdProfessor(Long idProfessor) {this.idProfessor = idProfessor;}

    public Long getIdHorario() {return idHorario;}

    public void setIdHorario(Long idHorario) {this.idHorario = idHorario;}

    public Long getIdPeriodo() {return idPeriodo;}

    public void setIdPeriodo(Long idPeriodo) {this.idPeriodo = idPeriodo;}

    public DiaSemana getDia() {return dia;}

    public void setDia(DiaSemana dia) {this.dia = dia;}
}
