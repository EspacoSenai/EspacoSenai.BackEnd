package com.api.reserva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
//Captura exceções globais com Status e Mensagem dedicada
public class ExceptionGlobal {

    /**
     * Trata exceções relacionadas à ausência de resultados em operações de consulta.
     * Este handler é acionado quando uma operação tenta recuperar uma entidade que não existe,
     * como ao buscar um registro por ID que não está no banco de dados.
     *
     * @param e A exceção de ausência de resultados capturada
     * @return ResponseEntity com status 404 (Not Found) e mensagem de erro detalhando o problema
     */
    //Trata requisicoes sem resultados
    @ExceptionHandler(SemResultadosException.class)
    public ResponseEntity<String> handler(SemResultadosException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handler(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Verifique o campo " + e.getMessage() + "e tente novamente.");
    }

    /**
     * Trata exceções de validação de argumentos de método.
     * Este handler captura erros gerados pelas anotações de validação (@Valid) nos controladores,
     * extraindo as mensagens de erro de cada campo e retornando-as como uma lista.
     *
     * @param e A exceção de validação que contém todos os erros de validação de campos
     * @return ResponseEntity com status 400 (Bad Request) e uma lista de mensagens de erro de validação
     */
    //Trata campos invalidos através de validação
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //Trata campos invalidos através de validação com lista
    public ResponseEntity<List<String>> handler (MethodArgumentNotValidException e){
        //Obtém os erros de validação de derivados de @Valid
        List<String> erros = e.getBindingResult()
                //Obtém a lista objetos com informações dos erros
                .getFieldErrors()
                //Transforma em stream
                .stream()
                //Para cada erro, formata a mensagem
                .map(erro -> String.format(erro.getDefaultMessage()))
                //Transforma em lista
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(erros);
    }

    /**
     * Trata exceções relacionadas a tentativas de inserção ou atualização com dados duplicados.
     *
     * @param e A exceção de dado duplicado capturada
     * @return ResponseEntity com status 409 (Conflict) e mensagem de erro detalhando o campo duplicado
     */

    @ExceptionHandler(DadoDuplicadoException.class)
    public ResponseEntity<String> handler (DadoDuplicadoException e) {
    //Trata erros internos no servidor
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler(HorarioInvalidoException.class)
    public ResponseEntity<String> handler (HorarioInvalidoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handler (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno no servidor. Tente novamente mais tarde.");
    }
}
