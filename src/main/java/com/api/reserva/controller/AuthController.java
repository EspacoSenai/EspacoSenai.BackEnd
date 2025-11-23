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


    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest loginRequest, 
                                                 jakarta.servlet.http.HttpServletResponse response) {
        LoginResponse loginResponse = tokenService.signIn(loginRequest);
        
        // Create HTTP-only cookie with JWT token
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("access_token", loginResponse.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(Math.toIntExact(loginResponse.expiresIn())); // Convert to seconds
        
        response.addCookie(cookie);
        
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> salvar(@Valid @RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {

        String token = gerarTokenCodigo();
        codigoService.criarContaEmCache(token, usuarioDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Se elegível para cadastro, " +
                                "um código de verificação foi enviado para " + usuarioDTO.getEmail(),
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

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(jakarta.servlet.http.HttpServletResponse response) {
        // Clear the access_token cookie
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("access_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie
        
        response.addCookie(cookie);
        
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Logout realizado com sucesso.");
    }
}
