package com.api.reserva.controller;

import com.api.reserva.dto.TurmaReferenciaDTO;
import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.dto.UsuarioReferenciaDTO;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.service.TurmaService;
import com.api.reserva.service.UsuarioService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;
//    @Autowired
//    private CodigoService codigoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TurmaService turmaService;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR')")
    @GetMapping("/buscar-todos")
    public ResponseEntity<List<UsuarioReferenciaDTO>> buscar(Authentication authentication) {
        List<UsuarioReferenciaDTO> usuarios = usuarioService.buscarTodos(authentication);
        return ResponseEntity.ok(usuarios);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar-por-id/{id}")
    public ResponseEntity<UsuarioReferenciaDTO> buscar(@PathVariable Long id) {
        UsuarioReferenciaDTO usuarioReferenciaDTO = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuarioReferenciaDTO);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/meu-perfil")
    public ResponseEntity<UsuarioReferenciaDTO> buscarMeuPerfil(Authentication authentication) {
        UsuarioReferenciaDTO usuarioReferenciaDTO = usuarioService.buscarMeuPerfil(authentication);
        return ResponseEntity.ok(usuarioReferenciaDTO);
    }

//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
//    @GetMapping("/buscar/tag/{tag}")
//    public ResponseEntity<UsuarioReferenciaDTO> buscarPorTag(@PathVariable String tag) {
//        UsuarioReferenciaDTO usuarioReferenciaDTO = usuarioService.buscarPorTag(tag);
//        return ResponseEntity.ok(usuarioReferenciaDTO);
//    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @PutMapping("/atualizar")
    public ResponseEntity<Object> atualizar (@Valid @RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {
        usuarioService.atualizar(usuarioDTO, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Usuário atualizado com sucesso.");
    }
//
//    @GetMapping("/confirmar-conta/{token}/{codigo}")
//    public ResponseEntity<Object> confirmarConta(@PathVariable String token, @PathVariable String codigo) {
//        usuarioService.confirmarConta(token, codigo);
//        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Conta confirmada e criada.");
//    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR')")
    @PostMapping("/salvar-privilegiado")
    public ResponseEntity<Object> salvarPrivilegiado(@Valid @RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {
        usuarioService.salvarPrivilegiado(usuarioDTO, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Usuário salvo com sucesso.");
    }

//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
//    @DeleteMapping("/deletar/{id}")
//    public ResponseEntity<Void> deletar(@PathVariable Long id) {
//        usuarioService.deletar(id);
//        return ResponseEntity.noContent().build();
//    }

    @PostMapping("/alterar-status/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    public ResponseEntity<Object> alterarStatus(@PathVariable Long id, Long idStatus) {
        usuarioService.alterarStatus(id, idStatus);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Status do usuário alterado com sucesso.");
    }

    @GetMapping("/minhas-turmas")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    public ResponseEntity<Set<TurmaReferenciaDTO>> minhasTurmas(Authentication authentication) {
        Set<TurmaReferenciaDTO> turmas = usuarioService.minhasTurmas(authentication);
        return ResponseEntity.ok(turmas);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR')")
    @GetMapping("/buscar-estudantes")
    public ResponseEntity<List<UsuarioReferenciaDTO>> buscarEstudantes() {
        List<UsuarioReferenciaDTO> estudantes = usuarioService.buscarEstudantes();
        return ResponseEntity.ok(estudantes);
    }
}

