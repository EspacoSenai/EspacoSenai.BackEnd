package com.api.reserva.exception;

public class DataInvalidaException extends RuntimeException {

    public DataInvalidaException(String message) {
        super(message);
    }

    public DataInvalidaException() {
        super("A data da reserva deve ser no dia da semana deste catalogo.");
    }
}
