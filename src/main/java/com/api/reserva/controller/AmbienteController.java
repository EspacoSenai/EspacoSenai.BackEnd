package com.api.reserva.controller;

import com.api.reserva.dto.AmbienteCategoria;
import com.api.reserva.dto.AmbienteDTO;
import com.api.reserva.dto.AmbienteReferenciaDTO;
import com.api.reserva.entity.Ambiente;
import com.api.reserva.service.AmbienteService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Ambiente salvo com sucesso.");
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar (@PathVariable Long id, @Valid @RequestBody AmbienteDTO ambienteDTO) {
        ambienteService.atualizar(id, ambienteDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Ambiente atualizado com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir (@PathVariable Long id) {
        ambienteService.excluir(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Ambiente excluído com sucesso.");
    }

    @PutMapping("/associarCategorias/{id}")
    public ResponseEntity<Object> associarCategorias(@PathVariable Long id, @RequestBody AmbienteCategoria ambienteCategoria) {
        ambienteService.associarCategorias(id, ambienteCategoria.getIdsCategorias());
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Categorias associadas.");
    }
}
