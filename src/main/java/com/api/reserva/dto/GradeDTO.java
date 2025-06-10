//package com.api.reserva.dto;
//
//import com.api.reserva.entity.Ambiente;
//import com.api.reserva.entity.Grade;
//import com.api.reserva.enums.DiaSemana;
//import com.api.reserva.enums.Disponibilidade;
//import jakarta.validation.constraints.NotNull;
//
//public class GradeDTO {
//    private Long id;
//    @NotNull(message = "Escolha um ambiente.")
//    private Long idAmbiente;
//    private Long idPeriodo;
//    @NotNull(message = "Escolha um dia da semana.")
//    private DiaSemana diaSemana;
//    @NotNull(message = "Escolha uma disponibilidade.")
//    private Disponibilidade disponibilidade;
//
//    public GradeDTO() {
//    }
//    public GradeDTO(Long idAmbiente, Long idPeriodo, DiaSemana diaSemana, Disponibilidade disponibilidade) {
//        this.idAmbiente = idAmbiente;
//        this.idPeriodo = idPeriodo;
//        this.diaSemana = diaSemana;
//        this.disponibilidade = disponibilidade;
//    }
//
//    public GradeDTO (Grade grade) {
//        id = grade.getId();
//        idAmbiente = grade.getAmbiente().getId();
//        idPeriodo = grade.getPeriodo().getId();
//        diaSemana = grade.getDiaSemana();
//        disponibilidade = grade.getDisponibilidade();
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public Long getIdAmbiente() {
//        return idAmbiente;
//    }
//
//    public void setIdAmbiente(Long idAmbiente) {
//        this.idAmbiente = idAmbiente;
//    }
//
//    public Long getIdPeriodo() {
//        return idPeriodo;
//    }
//
//    public void setIdPeriodo(Long idPeriodo) {
//        this.idPeriodo = idPeriodo;
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
