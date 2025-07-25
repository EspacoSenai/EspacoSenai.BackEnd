package com.api.reserva.controller;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.entity.Usuario;
import com.api.reserva.service.UsuarioService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioDTO>> listarTudo() {
            List<UsuarioDTO> usuarios = usuarioService.listar();
            return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<UsuarioDTO> listar(@PathVariable Long id) {
            UsuarioDTO usuario = usuarioService.listar(id);
            return ResponseEntity.ok(usuario);
    }

    @PostMapping("/salvar/estudante")
    public ResponseEntity<Object> salvarEstudante(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.salvarEstudante(usuarioDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Usuário criado com sucesso.");
    }

    @PostMapping("/salvar/estudantes/planilha")
    public ResponseEntity<List<Usuario>> salvarEstudantesPlanilha(@RequestParam MultipartFile planilha) {
        return ResponseEntity.ok(usuarioService.salvarEstudantesPlanilha(planilha));
    }

    @PostMapping("/salvar/interno")
    public ResponseEntity<Object> salvarInterno(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.salvarInterno(usuarioDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Usuário criado com sucesso.");
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar (@Valid @RequestBody UsuarioDTO usuarioDTO, @PathVariable Long id) {
        usuarioService.atualizar(id, usuarioDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Usuário atualizado com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT , "Usuário excluído com sucesso.");
    }
}
