package com.api.reserva.exception;

public class EntidadeJaExistente extends RuntimeException {
    public EntidadeJaExistente(String message) {
        super(String.format("%s já existe.", message));
    }
}
