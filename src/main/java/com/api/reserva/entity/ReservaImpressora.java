package com.api.reserva.entity;

import com.api.reserva.dto.TemperaturaDTO;
import com.api.reserva.enums.StatusReserva3D;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table(name = "tb_reserva_impressora")
@Entity
public class ReservaImpressora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private Usuario host;


    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    private StatusReserva3D statusReserva;

    @CurrentTimestamp
    private LocalDateTime dataHoraSolicitacao;

    @Column(unique = true)
    private Integer pin;

    private Double temperatura;


    public ReservaImpressora() {
    }

    public ReservaImpressora(
            Usuario host,
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim,
            LocalDateTime dataHoraSolicitacao,
            Integer pin) {
        this.host = host;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.dataHoraSolicitacao = dataHoraSolicitacao;
        this.pin = pin;
    }
    public ReservaImpressora(TemperaturaDTO temperaturaDTO) {
        this.temperatura = temperaturaDTO.temperatura();
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getHost() {
        return host;
    }

    public void setHost(Usuario host) {
        this.host = host;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public StatusReserva3D getStatusReserva() {
        return statusReserva;
    }

    public void setStatusReserva(StatusReserva3D statusReserva) {
        this.statusReserva = statusReserva;
    }

    public LocalDateTime getDataHoraSolicitacao() {
        return dataHoraSolicitacao;
    }

    public void setDataHoraSolicitacao(LocalDateTime dataHoraSolicitacao) {
        this.dataHoraSolicitacao = dataHoraSolicitacao;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }
}