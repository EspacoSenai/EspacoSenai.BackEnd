package com.api.reserva.controller;

import com.api.reserva.dto.TurmaDTO;
import com.api.reserva.service.TurmaService;
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
@RequestMapping("/turma")
public class TurmaController {
    @Autowired
    private TurmaService turmaService;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<TurmaDTO>> buscar() {
        List<TurmaDTO> turmas = turmaService.buscar();
        return ResponseEntity.ok(turmas);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR')")
    @GetMapping("/buscar/{id}")
    public ResponseEntity<TurmaDTO> buscar(@PathVariable Long id) {
        TurmaDTO turma = turmaService.buscar(id);
        return ResponseEntity.ok(turma);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PostMapping("/salvar")
    public ResponseEntity<Object> criar(@Valid @RequestBody TurmaDTO turmaDTO, Authentication authentication) {
        turmaService.salvar(turmaDTO, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Turma criada com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@Valid @RequestBody TurmaDTO turmaDTO, @PathVariable Long id) {
        turmaService.atualizar(id, turmaDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Turma atualizada com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PostMapping("/adicionarestudantes/{turmaId}")
    public ResponseEntity<Object> adicionarEstudantes(@PathVariable Long turmaId, @RequestBody List<Long> estudanteIds) {
        turmaService.adicionarEstudantes(turmaId, estudanteIds);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Estudantes adicionados com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @DeleteMapping("/removerestudantes/{turmaId}")
    public ResponseEntity<Object> removerEstudantes(@PathVariable Long turmaId, @RequestBody List<Long> estudanteIds) {
        turmaService.removerEstudantes(turmaId, estudanteIds);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Estudantes removidos com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PatchMapping("/atualizarprofessor/{turmaId}/{professorId}")
    public ResponseEntity<Object> atualizarProfessor(@PathVariable Long turmaId, @PathVariable Long professorId) {
        turmaService.atualizarProfessor(turmaId, professorId);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Professor atualizado com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR','SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    @PatchMapping("/ingressar-por-codigo/{codigoAcesso}")
    public ResponseEntity<Object> ingressarPorCodigo(@PathVariable String codigoAcesso, Authentication authentication) {
        turmaService.ingressarPorCodigo(codigoAcesso, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Ingresso na turma realizado com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PatchMapping("/gerar-novo-codigo/{turmaId}")
    public ResponseEntity<Object> gerarNovoCodigo(@PathVariable Long turmaId, Authentication authentication) {
        turmaService.gerarNovoCodigo(turmaId, authentication);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Novo código de acesso gerado com sucesso.");
    }

    @DeleteMapping("/deletar/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication authentication) {
        turmaService.deletar(id, authentication);
        return ResponseEntity.noContent().build();
    }
}