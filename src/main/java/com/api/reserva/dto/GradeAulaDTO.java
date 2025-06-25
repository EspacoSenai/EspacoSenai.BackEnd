package com.api.reserva.dto;

import com.api.reserva.entity.Disciplina;
import com.api.reserva.entity.GradeAula;
import com.api.reserva.enums.DiaSemana;
import jakarta.validation.constraints.NotNull;

public class GradeAulaDTO {

    private Long id;
    
    @NotNull(message = "Sala é obrigatória")
    private String sala;
    
    private UsuarioDTO professor;  // Para resposta
    private HorarioDTO horario;    // Para resposta
    private PeriodoDTO periodo;    // Para resposta
    
    @NotNull(message = "Dia é obrigatório")
    private DiaSemana dia;
    
    private DisciplinaDTO disciplina; // Para resposta
    
    // Campos para receber IDs na requisição
    @NotNull(message = "ID do professor é obrigatório")
    private Long idProfessor;
    
    @NotNull(message = "ID da disciplina é obrigatório")
    private Long idDisciplina;
    
    @NotNull(message = "ID do horário é obrigatório")
    private Long idHorario;
    
    @NotNull(message = "ID do período é obrigatório")
    private Long idPeriodo;

    public GradeAulaDTO() {}

    public GradeAulaDTO(Long id, String sala, UsuarioDTO professor, DisciplinaDTO disciplina, HorarioDTO horario, PeriodoDTO periodo, DiaSemana dia) {
        this.id = id;
        this.sala = sala;
        this.professor = professor;
        this.disciplina = disciplina;
        this.horario = horario;
        this.periodo = periodo;
        this.dia = dia;
    }

    public GradeAulaDTO(GradeAula entity) {
        this.id = entity.getId();
        this.sala = entity.getSala();
        this.professor = new UsuarioDTO(entity.getProfessor());
        this.disciplina = new DisciplinaDTO(entity.getDisciplina());
        this.horario = new HorarioDTO(entity.getHorario());
        this.periodo = new PeriodoDTO(entity.getPeriodo());
        this.dia = entity.getDia();
    }

    public Long getId() { return id; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public UsuarioDTO getProfessor() { return professor; }
    public void setProfessor(UsuarioDTO professor) { this.professor = professor; }

    public DisciplinaDTO getDisciplina() { return disciplina; }
    public void setDisciplina(DisciplinaDTO disciplina) { this.disciplina = disciplina; }

    public HorarioDTO getHorario() { return horario; }
    public void setHorario(HorarioDTO horario) { this.horario = horario; }

    public PeriodoDTO getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoDTO periodo) { this.periodo = periodo; }

    public DiaSemana getDia() { return dia; }
    public void setDia(DiaSemana dia) { this.dia = dia; }

    // Getters e Setters para os IDs
    public Long getIdProfessor() { return idProfessor; }
    public void setIdProfessor(Long idProfessor) { this.idProfessor = idProfessor; }

    public Long getIdDisciplina() { return idDisciplina; }
    public void setIdDisciplina(Long idDisciplina) { this.idDisciplina = idDisciplina; }

    public Long getIdHorario() { return idHorario; }
    public void setIdHorario(Long idHorario) { this.idHorario = idHorario; }

    public Long getIdPeriodo() { return idPeriodo; }
    public void setIdPeriodo(Long idPeriodo) { this.idPeriodo = idPeriodo; }
}