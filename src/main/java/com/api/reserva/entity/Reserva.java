package com.api.reserva.entity;

import com.api.reserva.enums.StatusReserva;
import com.api.reserva.util.CodigoUtil;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private Usuario host;

    @ManyToMany
    @JoinTable(
            name = "tb_reserva_participantes",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> membros = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "catalogo_id")
    private Catalogo catalogo;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    private StatusReserva statusReserva;

    @Column(length = 500)
    private String finalidade;

    @Column(unique = true, nullable = false, length = 5)
    private String codigo;

    @CurrentTimestamp
    private LocalDateTime criadoEm;

    public Reserva() {
    }

    public Reserva(Usuario host, Catalogo catalogo, LocalDate data, LocalTime horaInicio,
                   LocalTime horaFim, String finalidade) {
        this.host = host;
        this.catalogo = catalogo;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.statusReserva = StatusReserva.PENDENTE;
        this.finalidade = finalidade;
        this.criadoEm = LocalDateTime.now();
        // Gerar código único de 5 caracteres alfanuméricos
        this.codigo = CodigoUtil.gerarCodigo(5);
    }

    public Long getId() {
        return id;
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

    public StatusReserva getStatusReserva() {
        return statusReserva;
    }

    public void setStatusReserva(StatusReserva statusReserva) {
        this.statusReserva = statusReserva;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String msgUsuario) {
        this.finalidade = msgUsuario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime dataHoraSolicitacao) {
        this.criadoEm = dataHoraSolicitacao;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public Set<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(Set<Usuario> convidados) {
        this.membros = convidados;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
