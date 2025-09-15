//package com.api.reserva.controller;
//
//import com.api.reserva.dto.DadosCodigoDTO;
//import com.api.reserva.dto.UsuarioDTO;
//import com.api.reserva.service.CodigoService;
//import com.api.reserva.service.EmailService;
//import com.api.reserva.service.TokenService;
//import com.api.reserva.util.ResponseBuilder;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//import java.util.UUID;
//
//@RestController
//public class CodigoController {
//
//    @Autowired
//    private CodigoService codigoService;
//    @Autowired
//    private TokenService tokenService;
//    @Autowired
//    private EmailService emailService;
//
//    public String gerarToken() {
//        return UUID.randomUUID().toString();
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<Object> criarCodigoEmailVerificacao(@Valid @RequestBody UsuarioDTO usuarioDTO) {
//
//        String token = gerarToken();
//        DadosCodigoDTO dadosCodigoDTO = codigoService.criarContaEmCache(token, usuarioDTO);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(Map.of(
//                        "status", HttpStatus.OK.value(),
//                        "message", "Se elegível para cadastro, " +
//                                "um código de verificação foi enviado para" + usuarioDTO.getEmail(),
//                        "token", token));
//    }
//
//    @PostMapping("/redefinir-senha")
//    public ResponseEntity<Object> criarCodigoRedefinirSenha(@RequestParam String email,
//                                                            @RequestParam String senha) {
//        String token = UUID.randomUUID().toString();
//        DadosCodigoDTO dadosCodigo = codigoService.criarCodigoRedefinirSenha(token, email, senha);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(Map.of(
//                        "status", HttpStatus.OK.value(),
//                        "message", "Código de redefinição de senha criado com sucesso",
//                        "token", token
//                ));
//    }
//
//    @PostMapping("/confirmar-reserva")
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
//    public ResponseEntity<Object> criarCodigoConfirmarReserva(
//            @RequestParam String email,
//            @RequestParam Long reservaId) {
//        String token = UUID.randomUUID().toString();
//        DadosCodigoDTO dadosCodigo = codigoService.criarCodigoConfirmarReserva(token, email, reservaId);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(Map.of(
//                        "status", HttpStatus.CREATED.value(),
//                        "message", "Código de confirmação de reserva criado com sucesso",
//                        "token", token,
//                        "codigo", dadosCodigo.getCodigo(),
//                        "email", dadosCodigo.getIdentificador(),
//                        "reservaId", reservaId
//                ));
//    }
//
//    @GetMapping("/verificar/{token}")
//    public ResponseEntity<Object> verificarCodigo(@PathVariable String token) {
//        DadosCodigoDTO dadosCodigo = codigoService.buscarCodigo(token);
//
//        if (dadosCodigo == null) {
//            return ResponseBuilder.respostaSimples(HttpStatus.NOT_FOUND, "Token não encontrado ou expirado");
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "status", HttpStatus.OK.value(),
//                "message", "Código encontrado",
//                "codigo", dadosCodigo.getCodigo(),
//                "email", dadosCodigo.getIdentificador(),
//                "finalidade", dadosCodigo.getFinalidade(),
//                "criadoEm", dadosCodigo.getCriadoEm()
//        ));
//    }
//
//    @PostMapping("/validar/{token}")
//    public ResponseEntity<Object> validarCodigo(
//            @PathVariable String token,
//            @RequestParam String codigo) {
//        DadosCodigoDTO dadosCodigo = codigoService.buscarCodigo(token);
//
//        if (dadosCodigo == null) {
//            return ResponseBuilder.respostaSimples(HttpStatus.NOT_FOUND, "Token não encontrado ou expirado");
//        }
//
//        if (!dadosCodigo.getCodigo().equals(codigo)) {
//            return ResponseBuilder.respostaSimples(HttpStatus.BAD_REQUEST, "Código inválido");
//        }
//
//        // Remove o código do cache após validação bem-sucedida
//        codigoService.deletarCodigo(token);
//
//        return ResponseEntity.ok(Map.of(
//                "status", HttpStatus.OK.value(),
//                "message", "Código validado com sucesso",
//                "finalidade", dadosCodigo.getFinalidade(),
//                "email", dadosCodigo.getIdentificador(),
//                "validado", true
//        ));
//    }
//
//    @DeleteMapping("/remover/{token}")
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
//    public ResponseEntity<Object> removerCodigo(@PathVariable String token) {
//        codigoService.deletarCodigo(token);
//        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Código removido com sucesso");
//    }
//
////    @PostMapping("/gerar-token")
////    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
////    public ResponseEntity<Object> gerarToken() {
////        String token = UUID.randomUUID().toString();
////        return ResponseEntity.status(HttpStatus.CREATED)
////                .body(Map.of(
////                    "status", HttpStatus.CREATED.value(),
////                    "message", "Token gerado com sucesso",
////                    "token", token
////                ));
////    }
//}
