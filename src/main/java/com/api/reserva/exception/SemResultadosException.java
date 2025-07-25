package com.api.reserva.exception;

public class SemResultadosException extends RuntimeException{
    private final String entidade;
    private final String operacao;

    public SemResultadosException(){
        super("Sem resultados.");
        this.entidade = null;
        this.operacao = null;
    }

    public SemResultadosException(String entidade){
        super(String.format("%s não encontrado(a).", entidade));
        this.entidade = entidade;
        this.operacao = null;
    }

    public SemResultadosException(String entidade, String operacao) {
        super(String.format("%s não encontrado(a) para %s.", entidade, operacao));
        this.entidade = entidade;
        this.operacao = operacao;
    }

    public String getOperacao() {
        return operacao;
    }

    public String getEntidade() {
        return entidade;
    }
}
