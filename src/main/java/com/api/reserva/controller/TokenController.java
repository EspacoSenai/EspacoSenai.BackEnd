package com.api.reserva.controller;

import com.api.reserva.dto.LoginRequest;
import com.api.reserva.dto.LoginResponse;
import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.service.TokenService;
import com.api.reserva.service.UsuarioService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioService usuarioService;

//    @PostMapping("/signup")
//    public ResponseEntity<Object> salvar(@Valid @RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {
//        usuarioService.salvar(usuarioDTO, authentication);
//        return ResponseBuilder.respostaSimples(HttpStatus.CREATED,
//                "Se elegível para o cadastro, um e-mail de confirmação foi enviado.");
//    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(tokenService.signIn(loginRequest));
    }
}
