package com.api.reserva.exception;

public class TurmaInvalidaException extends RuntimeException {

    public TurmaInvalidaException(String message) {
        super(message);
    }

    public TurmaInvalidaException() {
        super("Você não está em uma turma válida para fazer uma reserva. A turma deve estar ativa (data de início já passou e data de término não foi atingida).");
    }
}

