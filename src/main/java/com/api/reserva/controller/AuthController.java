package com.api.reserva.controller;

import com.api.reserva.dto.DadosCodigoDTO;
import com.api.reserva.dto.LoginRequest;
import com.api.reserva.dto.LoginResponse;
import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.service.CodigoService;
import com.api.reserva.service.PreCadastroService;
import com.api.reserva.service.TokenService;
import com.api.reserva.service.UsuarioService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CodigoService codigoService;
    @Autowired
    private PreCadastroService preCadastroService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private String gerarTokenCodigo() {
        return UUID.randomUUID().toString();
    }

//    @PostMapping("/signup")
//    public ResponseEntity<Object> salvar(@Valid @RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {
//
//        if(preCadastroService.verificarElegibilidade(usuarioDTO.getEmail())) {
//
//        }
//
//        return ResponseBuilder.respostaSimples(HttpStatus.CREATED,
//                "Se elegível para o cadastro, um e-mail de confirmação foi enviado.");
//    }

    @PostMapping("/signup")
    public ResponseEntity<Object> salvar(@Valid @RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {

        String token = gerarTokenCodigo();

        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            if (preCadastroService.isElegivel(usuarioDTO.getEmail())) {
                DadosCodigoDTO dadosCodigoDTO = codigoService.criarContaEmCache(token, usuarioDTO);
            }
        } else {
            throw new UsuarioDuplicadoException();
        }

        return ResponseBuilder.respostaSimples(HttpStatus.OK,
                "Se elegível para o cadastro, um e-mail de confirmação foi enviado.");
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(tokenService.signIn(loginRequest));
    }

    @GetMapping("/confirmar-conta/{token}/{codigo}")
    public ResponseEntity<Object> confirmarConta(@PathVariable String token, @PathVariable String codigo) {
        usuarioService.confirmarConta(token, codigo);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Conta confirmada com sucesso.");
    }



}
