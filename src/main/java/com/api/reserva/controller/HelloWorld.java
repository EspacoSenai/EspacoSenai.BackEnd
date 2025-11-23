package com.api.reserva.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api")
    public String helloWorld() {
        return "Hello, World!";
    }

    @GetMapping(value = "hello")
    public String verificarBancoDeDados() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return "Conexão com o banco de dados bem-sucedida!";
        } catch (Exception e) {
            return "Falha ao conectar com o banco de dados: " + e.getMessage();
        }
    }
}
