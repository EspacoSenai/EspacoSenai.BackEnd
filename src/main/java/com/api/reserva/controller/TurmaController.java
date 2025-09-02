package com.api.reserva.controller;

import com.api.reserva.dto.TurmaDTO;
import com.api.reserva.service.TurmaService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turma")
public class TurmaController {
    @Autowired
    private TurmaService turmaService;

    @GetMapping("/buscar")
    public ResponseEntity<List<TurmaDTO>> buscar() {
        List<TurmaDTO> turmas = turmaService.buscar();
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<TurmaDTO> buscar(@PathVariable Long id) {
        TurmaDTO turma = turmaService.buscar(id);
        return ResponseEntity.ok(turma);
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> criar(@Valid @RequestBody TurmaDTO turmaDTO) {
        turmaService.salvar(turmaDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Turma criada com sucesso.");
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@Valid @RequestBody TurmaDTO turmaDTO, @PathVariable Long id) {
        turmaService.atualizar(turmaDTO, id);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Turma atualizada com sucesso.");
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        turmaService.deletar(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Turma excluída com sucesso.");
    }

    @PutMapping("/definirestudantes/{turmaId}")
    public ResponseEntity<Object> definirEstudantes(@PathVariable Long turmaId, @RequestBody List<Long> estudanteIds) {
        turmaService.definirEstudantes(turmaId, estudanteIds);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Estudantes associados à turma com sucesso.");
    }


//    @PostMapping("/associarestudante/{turmaId}/{estudanteId}")
//    public ResponseEntity<Object> associarEstudante(@PathVariable Long turmaId, @PathVariable Long estudanteId) {
//        turmaService.associarEstudante(turmaId, estudanteId);
//        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Estudante associado à turma com sucesso.");
//    }
//
//    @DeleteMapping("/desassociarestudante/{turmaId}/{estudanteId}")
//    public ResponseEntity<Object> desassociarEstudante(@PathVariable Long turmaId, @PathVariable Long estudanteId) {
//        turmaService.desassociarEstudante(turmaId, estudanteId);
//        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Estudante removido da turma com sucesso.");
//    }

}