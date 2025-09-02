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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("ambiente")
public class  AmbienteController {

    @Autowired
    private AmbienteService ambienteService;

    @GetMapping("/buscar")
    public ResponseEntity<List<AmbienteReferenciaDTO>> buscar() {
        return ResponseEntity.ok(ambienteService.buscar());
    }

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
    public ResponseEntity<Object> atualizar (@PathVariable Long id, @Valid @RequestBody AmbienteDTO ambienteDTO) {
        ambienteService.atualizar(id, ambienteDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Ambiente atualizado com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletar (@PathVariable Long id) {
        ambienteService.deletar(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Ambiente excluído com sucesso.");
    }

//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
//    @PutMapping("/associarcategorias/{id}")
//    public ResponseEntity<Object> associarCategorias(@PathVariable Long ambienteId, @RequestBody Set<Long> categoriasIds) {
//        ambienteService.associarCategorias(categoriasIds, ambienteId);
//        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Categorias associadas.");
//    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @PutMapping("/associarresponsaveis/{ambienteId}")
    public ResponseEntity<Object> associarResponsaveis(@PathVariable Long ambienteId, @RequestBody Set<Long> responsaveisIds) {
        ambienteService.associarResponsaveis(ambienteId, responsaveisIds);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Responsáveis pelo ambiente adicionados.");
    }
}
