package com.api.reserva.exception;

public class CodigoInvalidoException extends RuntimeException{
    public CodigoInvalidoException() {
        super("Código inválido ou expirado.");
    }
}
