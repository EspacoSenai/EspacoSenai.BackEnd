package com.api.reserva.exception;

public class SemPermissaoException extends RuntimeException {
    public SemPermissaoException(String message) {
        super(message);
    }
}
