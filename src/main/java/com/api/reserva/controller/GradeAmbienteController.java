package com.api.reserva.controller;

import com.api.reserva.dto.GradeAmbienteDTO;
import com.api.reserva.dto.GradeAmbienteReferenciaDTO;
import com.api.reserva.repository.GradeAmbienteRepository;
import com.api.reserva.service.GradeAmbienteService;
import com.api.reserva.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("gradeambiente")
public class GradeAmbienteController {
    @Autowired
    private GradeAmbienteService gradeAmbienteService;

    @GetMapping("/listar")
    public ResponseEntity<List<GradeAmbienteReferenciaDTO>> listar() {
        return ResponseEntity.ok(gradeAmbienteService.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<GradeAmbienteReferenciaDTO> listar (@PathVariable Long id) {
        return ResponseEntity.ok(gradeAmbienteService.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@RequestBody GradeAmbienteDTO gradeAmbienteDTO) {
        gradeAmbienteService.salvar(gradeAmbienteDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Grade de ambiente salva com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody GradeAmbienteDTO gradeAmbienteDTO) {
        gradeAmbienteService.atualizar(id, gradeAmbienteDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Grade de ambiente atualizada com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir (@PathVariable Long id) {
        gradeAmbienteService.excluir(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Grade de ambiente excluídanb com sucesso.");
    }
}
