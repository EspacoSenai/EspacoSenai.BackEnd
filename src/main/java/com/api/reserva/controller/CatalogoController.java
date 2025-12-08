package com.api.reserva.controller;

import com.api.reserva.dto.CatalogoDTO;
import com.api.reserva.dto.CatalogoReferenciaDTO;
import com.api.reserva.service.CatalogoService;
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
@RequestMapping("catalogo")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar")
    public ResponseEntity<List<CatalogoReferenciaDTO>> buscar() {
        return ResponseEntity.ok(catalogoService.buscar());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar/{id}")
    public ResponseEntity<CatalogoReferenciaDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.buscar(id));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR')")
    @GetMapping("/buscar-por-ambiente/{ambienteId}")
    public ResponseEntity<Set<CatalogoReferenciaDTO>> buscarPorAmbiente(@PathVariable Long ambienteId) {
        Set<CatalogoReferenciaDTO> catalogos = catalogoService.buscarPorAmbienteId(ambienteId);
        return ResponseEntity.ok(catalogos);
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @PostMapping("/salvar/{ambienteId}")
    public ResponseEntity<Object> salvar(@PathVariable Long ambienteId, @Valid @RequestBody Set<CatalogoDTO> catalogosDTO, Authentication authentication) {
        catalogoService.salvar(ambienteId, catalogosDTO, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Catálogos salvos com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @PatchMapping("/atualizar/{ambienteId}")
    public ResponseEntity<Object> atualizar(@PathVariable Long ambienteId, @Valid @RequestBody Set<CatalogoDTO> catalogosDTO, Authentication authentication) {
        catalogoService.atualizar(ambienteId, catalogosDTO, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Catálogos atualizados com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @DeleteMapping("/deletar")
    public ResponseEntity<Object> deletar(@RequestBody Set<Long> catalogosIds, Authentication authentication) {
        catalogoService.deletar(catalogosIds, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Catálogos excluídos com sucesso.");
    }
}
