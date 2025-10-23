package com.api.reserva.exception;

public class SemPermissaoException extends RuntimeException {
    
    public SemPermissaoException() {
        super("Usuário sem permissão para realizar esta operação");
    }
    
    public SemPermissaoException(String message) {
        super(message);
    }
    
    public SemPermissaoException(String message, Throwable cause) {
        super(message, cause);
    }
}