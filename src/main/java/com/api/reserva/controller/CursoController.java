package com.api.reserva.controller;

import com.api.reserva.dto.CursoDTO;
import com.api.reserva.service.CursoService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador responsável por gerenciar as requisições relacionadas a entidade Curso.
 */
@RestController
@RequestMapping("curso")
public class CursoController {
    
    @Autowired
    private CursoService cursoService;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR' ,'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar")
    public ResponseEntity<List<CursoDTO>> buscar() {
        return ResponseEntity.ok(cursoService.buscar());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR' ,'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar/{id}")
    public ResponseEntity<CursoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.buscar(id));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody CursoDTO cursoDTO) {
        cursoService.salvar(cursoDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Curso salvo com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@Valid @RequestBody CursoDTO cursoDTO, @PathVariable Long id) {
        cursoService.atualizar(cursoDTO, id);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Curso atualizado com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cursoService.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
