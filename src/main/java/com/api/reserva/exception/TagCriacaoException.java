package com.api.reserva.exception;

public class TagCriacaoException extends RuntimeException{
    public TagCriacaoException() {
        super("Erro ao gerar TAG.");
    }
}
