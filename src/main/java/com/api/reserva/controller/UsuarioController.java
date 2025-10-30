package com.api.reserva.controller;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.dto.UsuarioReferenciaDTO;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.service.UsuarioService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;
//    @Autowired
//    private CodigoService codigoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioReferenciaDTO>> buscar() {
            List<UsuarioReferenciaDTO> usuarios = usuarioService.buscar();
            return ResponseEntity.ok(usuarios);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @GetMapping("/buscar/{id}")
    public ResponseEntity<UsuarioReferenciaDTO> buscar(@PathVariable Long id) {
            UsuarioReferenciaDTO usuarioReferenciaDTO = usuarioService.buscar(id);
            return ResponseEntity.ok(usuarioReferenciaDTO);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar/tag/{tag}")
    public ResponseEntity<UsuarioReferenciaDTO> buscarPorTag(@PathVariable String tag) {
        UsuarioReferenciaDTO usuarioReferenciaDTO = usuarioService.buscarPorTag(tag);
        return ResponseEntity.ok(usuarioReferenciaDTO);
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar (@Valid @RequestBody UsuarioDTO usuarioDTO, @PathVariable Long id) {
        usuarioService.atualizar(id, usuarioDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Usuário atualizado com sucesso.");
    }
//
//    @GetMapping("/confirmar-conta/{token}/{codigo}")
//    public ResponseEntity<Object> confirmarConta(@PathVariable String token, @PathVariable String codigo) {
//        usuarioService.confirmarConta(token, codigo);
//        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Conta confirmada e criada.");
//    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @PostMapping("/salvar-privilegiado")
    public ResponseEntity<Object> salvarPrivilegiado(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.salvarPrivilegiado(usuarioDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Usuário salvo com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
