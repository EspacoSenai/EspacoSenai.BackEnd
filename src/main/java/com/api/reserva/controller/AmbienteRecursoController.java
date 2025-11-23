package com.api.reserva.controller;

import com.api.reserva.dto.AmbienteRecursoDTO;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.service.AmbienteRecursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ambiente-recursos")
public class AmbienteRecursoController {

    private final AmbienteRecursoService service;

    public AmbienteRecursoController(AmbienteRecursoService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping
    public ResponseEntity<List<AmbienteRecursoDTO>> listar() {
        return ResponseEntity.ok(service.buscar());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/{id}")
    public ResponseEntity<AmbienteRecursoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody AmbienteRecursoDTO dto) {
        service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(SemResultadosException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(SemResultadosException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }
}

