package com.api.reserva.controller;

import com.api.reserva.dto.AmbienteDTO;
import com.api.reserva.dto.AmbienteReferenciaDTO;
import com.api.reserva.service.AmbienteService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ambiente")
public class  AmbienteController {

    @Autowired
    private AmbienteService ambienteService;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar")
    public ResponseEntity<List<AmbienteReferenciaDTO>> buscar() {
        return ResponseEntity.ok(ambienteService.buscar());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar/{id}")
    public ResponseEntity<AmbienteReferenciaDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ambienteService.buscar(id));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody AmbienteDTO ambienteDTO) {
        ambienteService.salvar(ambienteDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Ambiente salvo com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar (@PathVariable Long id, @Valid @RequestBody AmbienteDTO ambienteDTO,
                                             Authentication authentication) {
        ambienteService.atualizar(id, ambienteDTO, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Ambiente atualizado com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletar (@PathVariable Long id) {
        ambienteService.deletar(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Ambiente excluído com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @PutMapping("/atribuir-responsavel/{ambienteId}/{responsavelId}")
    public ResponseEntity<Object> atribuirResponsavel(@PathVariable Long ambienteId, @PathVariable Long responsavelId) {
        ambienteService.atribuirResponsavel(ambienteId, responsavelId);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Responsável atribuído ao ambiente com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @PatchMapping("/indisponibilizar/{id}")
    public ResponseEntity<Object> indisponibilizarAmbiente(@PathVariable Long id) {
        ambienteService.indisponibilizarAmbiente(id);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Ambiente indisponibilizado. Todas as reservas pendentes foram canceladas.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @PatchMapping("/disponibilizar/{id}")
    public ResponseEntity<Object> disponibilizarAmbiente(@PathVariable Long id) {
        ambienteService.disponibilizarAmbiente(id);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Ambiente disponibilizado. Todos os catálogos foram reativados.");
    }
}
