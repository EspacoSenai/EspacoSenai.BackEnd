package com.api.reserva.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class ResponseBuilder {

    public static ResponseEntity<Object> respostaSimples(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("status", status.value(), "message", message));
    }

    public static ResponseEntity<Object> respostaLista(HttpStatus status, List<String> erros) {
        return ResponseEntity.status(status).body(Map.of("status", status.value(), "message", erros));
    }
}
