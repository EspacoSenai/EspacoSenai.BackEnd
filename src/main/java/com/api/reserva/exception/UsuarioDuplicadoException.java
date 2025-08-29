package com.api.reserva.exception;

public class UsuarioDuplicadoException extends RuntimeException{
    public UsuarioDuplicadoException(String message){
        super(message);
    }

    public UsuarioDuplicadoException(){
        super("Email já existente.");
    }
}
