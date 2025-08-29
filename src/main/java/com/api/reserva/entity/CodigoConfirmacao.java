//package com.api.reserva.entity;
//
//import jakarta.persistence.*;
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.Random;
//
//@Entity
//@Table(name = "tb_codigo_confirmacao")
//public class CodigoConfirmacao {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 6)
//    private String codigo;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TipoCodigo tipoCodigo;
//
//    @Column(nullable = false)
//    private Long entidadeId;
//
//    @Column(nullable = false)
//    @CreationTimestamp
//    private LocalDateTime criadoEm;
//
//    @Column(nullable = false)
//    private LocalDateTime expiraEm;
//
//    public enum TipoCodigo {
//        EMAIL_VERIFICACAO,
//        CONFIRMACAO_RESERVA,
//        REDEFINICAO_SENHA;
//    }
//
////    private void gerarCodigo() {
////        SecureRandom random = new SecureRandom();
////        Integer codigo = random.nextInt(999999) + 1;
////        this.codigo = String.format("%06d", codigo);
////    }
//
//    private void gerarCodigo(int lenght) {
//        SecureRandom random = new SecureRandom();
//        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//        StringBuilder sb = new StringBuilder(lenght);
//
//        for(int i = 0; i < lenght ; i++) {
//            int index = random.nextInt(caracteres.length());
//            sb.append(caracteres.charAt(index));
//        }
//        this.codigo = sb.toString();
//    }
//
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getCodigo() {
//        return codigo;
//    }
//
//    public void setCodigo(String codigo) {
//        this.codigo = codigo;
//    }
//
//    public String getDestino() {
//        return destino;
//    }
//
//    public void setDestino(String destino) {
//        this.destino = destino;
//    }
//
//    public TipoCodigo getTipoCodigo() {
//        return tipoCodigo;
//    }
//
//    public void setTipoCodigo(TipoCodigo tipoCodigo) {
//        this.tipoCodigo = tipoCodigo;
//    }
//
//    public Long getEntidadeId() {
//        return entidadeId;
//    }
//
//    public void setEntidadeId(Long entidadeId) {
//        this.entidadeId = entidadeId;
//    }
//
//    public LocalDateTime getCriadoEm() {
//        return criadoEm;
//    }
//
//    public void setCriadoEm(LocalDateTime criadoEm) {
//        this.criadoEm = criadoEm;
//    }
//
//    public LocalDateTime getExpiraEm() {
//        return expiraEm;
//    }
//
//    public void setExpiraEm(LocalDateTime expiraEm) {
//        this.expiraEm = expiraEm;
//    }
//}
