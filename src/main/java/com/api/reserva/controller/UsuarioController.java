package com.api.reserva.controller;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService service;

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioDTO>> listarTudo() {
        List<UsuarioDTO> usuarios = service.listar();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<UsuarioDTO> listar(@PathVariable Long id) {
        UsuarioDTO usuario = service.listar(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/salvar")
    public ResponseEntity<String> salvar(@Valid @RequestBody UsuarioDTO user) {
        service.salvar(user);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário cadastrado com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizar (@Valid @RequestBody UsuarioDTO usuarioDTO, @PathVariable Long id){
        service.atualizar(usuarioDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário atualizado com sucesso.");    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário excluído com sucesso.");
    }
}