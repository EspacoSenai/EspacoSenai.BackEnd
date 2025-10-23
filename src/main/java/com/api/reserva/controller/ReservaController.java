package com.api.reserva.controller;

import com.api.reserva.dto.ReservaDTO;
import com.api.reserva.dto.ReservaReferenciaDTO;
import com.api.reserva.service.ReservaService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("reserva")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<ReservaReferenciaDTO>> buscar() {
        return ResponseEntity.ok(reservaService.buscar());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar/usuario")
    public ResponseEntity<List<ReservaReferenciaDTO>> buscarPorUsuario(Authentication authentication) {
        return ResponseEntity.ok(reservaService.buscarPorUsuario(authentication));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @GetMapping("/buscar/{id}")
    public ResponseEntity<ReservaReferenciaDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscar(id));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody ReservaDTO reservaDTO, Authentication authentication) {
        reservaService.salvar(reservaDTO, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Pedido de reserva criado. Aguarde aprovação.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @Valid @RequestBody ReservaDTO reservaDTO, Authentication authentication) {
        reservaService.atualizar(id, reservaDTO, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Reserva atualizada com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id, Authentication authentication) {
        reservaService.deletar(id, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Reserva excluída com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @PatchMapping("/aprovar/{id}")
    public ResponseEntity<Object> aprovar(@PathVariable Long id, Authentication authentication) {
        reservaService.aprovar(id, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Reserva aprovada com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @PatchMapping("/rejeitar/{id}")
    public ResponseEntity<Object> rejeitar(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        String motivo = body.get("motivo");
        reservaService.rejeitar(id, motivo, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Reserva rejeitada com sucesso.");
    }
}
