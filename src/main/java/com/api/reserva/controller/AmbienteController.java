package com.api.reserva.controller;

import com.api.reserva.dto.AmbienteDTO;
import com.api.reserva.dto.AmbienteReferenciaDTO;
import com.api.reserva.service.AmbienteService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ambiente")
public class  AmbienteController {

    @Autowired
    private AmbienteService ambienteService;

    @GetMapping("/listar")
    public ResponseEntity<List<AmbienteReferenciaDTO>> listar() {
        return ResponseEntity.ok(ambienteService.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<AmbienteReferenciaDTO> listar(@PathVariable Long id) {
        return ResponseEntity.ok(ambienteService.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody AmbienteDTO ambienteDTO) {
        ambienteService.salvar(ambienteDTO);
        return ResponseBuilder.buildMessage(HttpStatus.CREATED, "Ambiente salvo com sucesso.");
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar (@PathVariable Long id, @Valid @RequestBody AmbienteDTO ambienteDTO) {
        ambienteService.atualizar(id, ambienteDTO);
        return ResponseBuilder.buildMessage(HttpStatus.OK, "Ambiente atualizado com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir (@PathVariable Long id) {
        ambienteService.excluir(id);
        return ResponseBuilder.buildMessage(HttpStatus.NO_CONTENT, "Ambiente excluído com sucesso.");
    }
}
