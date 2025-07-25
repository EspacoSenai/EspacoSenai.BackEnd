package com.api.reserva.exception;

import com.api.reserva.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
//Captura exceções globais com Status e Mensagem dedicada
public class ExceptionGlobal {

    @ExceptionHandler(SemResultadosException.class)
    public ResponseEntity<Object> handler(SemResultadosException e) {
        return ResponseBuilder.respostaSimples(HttpStatus.NOT_FOUND, e.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handler(IllegalArgumentException e){
        return ResponseBuilder.respostaSimples(HttpStatus.BAD_REQUEST, String.format(
                "Verifique o campo: '%s' e tente novamente.", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handler(MethodArgumentNotValidException e) {
        List<String> erros = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .toList();
        return ResponseBuilder.respostaLista(HttpStatus.BAD_REQUEST, erros);
    }

    @ExceptionHandler(DadoDuplicadoException.class)
    public ResponseEntity<Object> handler (DadoDuplicadoException e) {
        return ResponseBuilder.respostaSimples(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DataInvalidaException.class)
    public ResponseEntity<Object> handler(DataInvalidaException e) {
        return ResponseBuilder.respostaSimples(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HorarioInvalidoException.class)
    public ResponseEntity<Object> handler (HorarioInvalidoException e) {
        return ResponseBuilder.respostaSimples(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UsuarioDuplicadoException.class)
    public ResponseEntity<Object> handler (UsuarioDuplicadoException e) {
        return ResponseBuilder.respostaSimples(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(EntidadeJaAssociadaException.class)
    public ResponseEntity<Object> handler(EntidadeJaAssociadaException e) {
        return ResponseBuilder.respostaSimples(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handler (Exception e){
        return ResponseBuilder.respostaSimples(HttpStatus.INTERNAL_SERVER_ERROR, String.format(
                "Erro interno servidor, tente novamente mais tarde. %s", e.getMessage()));
    }
}
