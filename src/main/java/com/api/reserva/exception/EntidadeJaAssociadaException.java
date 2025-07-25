package com.api.reserva.exception;

public class EntidadeJaAssociadaException extends RuntimeException {

    public EntidadeJaAssociadaException() {
        super("Estas entidades já estão associadas.");
    }

    public EntidadeJaAssociadaException(String message) {
        super(message);
    }
}
