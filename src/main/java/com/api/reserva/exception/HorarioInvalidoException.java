package com.api.reserva.exception;

public class HorarioInvalidoException extends RuntimeException{
    public HorarioInvalidoException() {
        super("O horário de início não pode ser maior ou igual ao horário de término.");
    }

    public HorarioInvalidoException(String message) {
        super(message);
    }
}
