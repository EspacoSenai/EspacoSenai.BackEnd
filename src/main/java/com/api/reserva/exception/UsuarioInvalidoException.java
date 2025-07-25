package com.api.reserva.exception;

public class UsuarioInvalidoException extends RuntimeException{
    private final String message;

    public UsuarioInvalidoException(String message){
        super(message);
        this.message = message;
    }
}
