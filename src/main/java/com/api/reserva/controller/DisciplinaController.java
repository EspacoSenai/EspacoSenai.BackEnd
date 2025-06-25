package com.api.reserva.controller;

import com.api.reserva.dto.DisciplinaDTO;
import com.api.reserva.service.DisciplinaService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("disciplina")
public class DisciplinaController {

    @Autowired
    private DisciplinaService service;

    @GetMapping("/listar")
    public ResponseEntity<List<DisciplinaDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<DisciplinaDTO> listar(@PathVariable Long id) {
        return ResponseEntity.ok(service.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody DisciplinaDTO dto) {
        service.salvar(dto);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Disciplina salva com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @Valid @RequestBody DisciplinaDTO dto) {
        service.atualizar(id, dto);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Disciplina atualizada com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Disciplina excluída com sucesso.");
    }
}
