//package com.api.reserva.entity;
//
//import com.api.reserva.dto.GradeDTO;
//import com.api.reserva.enums.DiaSemana;
//import com.api.reserva.enums.Disponibilidade;
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "tb_grade")
//public class Grade {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    @ManyToOne
//    @JoinColumn(name = "id_ambiente")
//
//    private Ambiente ambiente;
//
//    @ManyToOne
//    @JoinColumn(name = "id_periodo")
//    private Periodo periodo;
//
//    @Enumerated(EnumType.STRING)
//    private DiaSemana diaSemana;
//
//    @Enumerated(EnumType.STRING)
//    private Disponibilidade disponibilidade;
//
//    public Grade() {
//    }
//
//    public Grade(Ambiente ambiente, Periodo periodo, DiaSemana diaSemana, Disponibilidade disponibilidade) {
//        this.ambiente = ambiente;
//        this.periodo = periodo;
//        this.diaSemana = diaSemana;
//        this.disponibilidade = disponibilidade;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public Ambiente getAmbiente() {
//        return ambiente;
//    }
//
//    public void setAmbiente(Ambiente idAmbiente) {
//        this.ambiente = idAmbiente;
//    }
//
//    public Periodo getPeriodo() {
//        return periodo;
//    }
//
//    public void setPeriodo(Periodo idPeriodo) {
//        this.periodo = idPeriodo;
//    }
//
//    public DiaSemana getDiaSemana() {
//        return diaSemana;
//    }
//
//    public void setDiaSemana(DiaSemana diaSemana) {
//        this.diaSemana = diaSemana;
//    }
//
//    public Disponibilidade getDisponibilidade() {
//        return disponibilidade;
//    }
//
//    public void setDisponibilidade(Disponibilidade disponibilidade) {
//        this.disponibilidade = disponibilidade;
//    }
//}
