package com.api.reserva.exception;

public class HorarioInvalidoException extends RuntimeException{
    public HorarioInvalidoException() {
        super("Verifique os horários informados.");
    }

    public HorarioInvalidoException(String message) {
        super(message);
    }
}
