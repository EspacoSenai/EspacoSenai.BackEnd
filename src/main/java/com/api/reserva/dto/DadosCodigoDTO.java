package com.api.reserva.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DadosCodigoDTO {

    private String codigo;

    private String email;

    private Finalidade finalidade;

    private Map<String, Object> dadosAdicionais;

    private LocalDateTime criadoEm;

    public DadosCodigoDTO() {
    }

    public DadosCodigoDTO(String codigo, String email, Finalidade finalidade) {
        this.codigo = codigo;
        this.email = email;
        this.finalidade = finalidade;
        this.criadoEm = LocalDateTime.now();
        this.dadosAdicionais = new HashMap<>();
    }

    public enum Finalidade {
        EMAIL_VERIFICACAO,
        CONFIRMACAO_RESERVA,
        REDEFINICAO_SENHA;
    }

    private void adicionarDadosAdicionais(String chave, String valor) {
        this.dadosAdicionais.put(chave, valor);
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Finalidade getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(Finalidade finalidade) {
        this.finalidade = finalidade;
    }

    public Object getDado(String key) {
        return this.dadosAdicionais.get(key);
    }

    public void setDadosAdicionais(Map<String, Object> dadosAdicionais) {
        this.dadosAdicionais = dadosAdicionais;
    }
}