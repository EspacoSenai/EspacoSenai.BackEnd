package com.api.reserva.controller;

import com.api.reserva.dto.GradeAulaDTO;
import com.api.reserva.service.GradeAulaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grade-aula")
public class GradeAulaController {

    @Autowired
    private GradeAulaService gradeAulaService;

    @GetMapping("/listar")
    public ResponseEntity<List<GradeAulaDTO>> listar() {
        return ResponseEntity.ok(gradeAulaService.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<GradeAulaDTO> listar(@PathVariable Long id) {
        return ResponseEntity.ok(gradeAulaService.buscarPorId(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody GradeAulaDTO dto) {
        gradeAulaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body("Aula salva com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@Valid @PathVariable Long id, @RequestBody GradeAulaDTO dto) {
        gradeAulaService.atualizar(id, dto);
        return ResponseEntity.status(HttpStatus.OK.value()).body("Aula atualizada com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id, @RequestParam Long idProfessor) {
        gradeAulaService.deletar(id, idProfessor);
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body("Aula excluída com sucesso.");
    }
}
