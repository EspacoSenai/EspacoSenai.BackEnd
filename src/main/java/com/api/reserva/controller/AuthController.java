package com.api.reserva.controller;

import com.api.reserva.dto.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("auth")
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

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(tokenService.signIn(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> salvar(@Valid @RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {

        String token = gerarTokenCodigo();
        codigoService.criarContaEmCache(token, usuarioDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Se elegível para cadastro, " +
                                "um código de verificação foi enviado para" + usuarioDTO.getEmail(),
                        "token", token));
    }

    @GetMapping("/confirmar-conta/{token}/{codigo}")
    public ResponseEntity<Object> confirmarConta(@PathVariable String token, @PathVariable String codigo) {
        usuarioService.confirmarConta(token, codigo);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Conta confirmada com sucesso.");
    }


    @PostMapping("/redefinir-senha")
    public ResponseEntity<Object> redefinirSenha (@Valid @RequestBody RedefinirSenhaRequest redefinirSenhaRequest) {
        String token = gerarTokenCodigo();
        codigoService.redefinirSenhaGerarCodigo(token, redefinirSenhaRequest.getIdentificador());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Código para redefinição de senha enviado por email.",
                        "token", token));
    }

    @GetMapping("/redefinir-senha/validar-codigo/{token}/{codigo}")
    public ResponseEntity<Object> confirmarNovaSenha(@PathVariable String token, @PathVariable String codigo) {
        usuarioService.redefinirSenhaValidarCodigo(token, codigo);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Código confirmado. Digite sua nova senha.");
    }

    @PostMapping("/redefinir-senha/nova-senha/{token}")
    public ResponseEntity<Object> redefinirSenhaNovaSenha(@PathVariable String token, @RequestBody NovaSenhaRequest novaSenhaRequest) {
        usuarioService.redefinirSenhaNovaSenha(token, novaSenhaRequest.getNovaSenha());
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Senha redefinida com sucesso.");
    }
}
