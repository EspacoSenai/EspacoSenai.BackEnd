package com.api.reserva.exception;

public class DadoInvalidoException extends RuntimeException {
    private final String campo;

    public DadoInvalidoException(String message){
        super(message);
        this.campo = null;
    }

    public DadoInvalidoException() {
        super("Verifique os campos e tente novamente.");
        this.campo = null;
    }

    public String getCampo() {
        return campo;
    }
}
