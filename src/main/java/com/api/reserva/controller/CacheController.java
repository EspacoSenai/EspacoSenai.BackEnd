package com.api.reserva.controller;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.service.CodigoService;
import com.api.reserva.service.UsuarioService;
import com.api.reserva.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CacheController {
    @Autowired
    private CodigoService codigoService;
    @Autowired
    private UsuarioService usuarioService;

    public String gerarToken() {
        return UUID.randomUUID().toString();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Object> cadastro(UsuarioDTO usuarioDTO) {
        codigoService.criarContaEmCache(gerarToken(), usuarioDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Verifique seu email e confirme sua conta.");
    }

    @GetMapping("/confirmar-email/{token}/{codigo}")
    public ResponseEntity<Object> confirmarEmail(String token, String email) {
        usuarioService.confirmarConta(token, email);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Conta confirmada.");
    }
}
