package com.api.reserva.controller;

import com.api.reserva.dto.AmbienteDTO;
import com.api.reserva.service.AmbienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ambiente")
public class  AmbienteController {
    @Autowired
    AmbienteService service;

    @GetMapping("/listar")
    public ResponseEntity<List<AmbienteDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<AmbienteDTO> listar(@PathVariable Long id) {
        return ResponseEntity.ok(service.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<AmbienteDTO> salvar(@Valid @RequestBody AmbienteDTO ambienteDTO) {
        return ResponseEntity.ok(service.salvar(ambienteDTO));
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<AmbienteDTO> atualizar (@Valid @PathVariable Long id, @Valid @RequestBody AmbienteDTO ambienteDTO) {
        return ResponseEntity.ok(service.atualizar(id, ambienteDTO));
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir (@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok().build();
    }
}
