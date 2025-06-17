package com.api.reserva.controller;

import com.api.reserva.dto.PeriodoDTO;
import com.api.reserva.service.PeriodoService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("periodo")
public class PeriodoController {
    @Autowired
    PeriodoService service;

    @GetMapping("/listar")
    public ResponseEntity<List<PeriodoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<PeriodoDTO> listar(@PathVariable Long id){
        return ResponseEntity.ok(service.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody PeriodoDTO periodoDTO) {
        service.salvar(periodoDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Periodo salvo com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @Valid @RequestBody PeriodoDTO periodoDTO) {
        service.atualizar(id, periodoDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Periodo atualizado com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Periodo excluido sucesso.");
    }
}
