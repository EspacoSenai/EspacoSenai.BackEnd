package com.api.reserva.exception;

public class DataInvalidaException extends RuntimeException {

    public DataInvalidaException() {
        super("Data incompatível com esta grade.");
    }
}
