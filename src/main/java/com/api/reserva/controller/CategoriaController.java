package com.api.reserva.controller;

import com.api.reserva.dto.CategoriaDTO;
import com.api.reserva.dto.CategoriaReferenciaDTO;
import com.api.reserva.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador responsável por gerenciar as requisições relacionadas a entidade Categoria.
 */

@RestController
@RequestMapping("categoria")
public class CategoriaController {
    @Autowired
    CategoriaService categoriaService;

    @GetMapping("/listar")
    public ResponseEntity<List<CategoriaDTO>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<CategoriaReferenciaDTO> listar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        categoriaService.salvar(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body("Categoria salva com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@Valid @PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        categoriaService.atualizar(id, categoriaDTO);
        return ResponseEntity.status(HttpStatus.OK.value()).body("Categoria atualizada com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body("Categoria excluída com sucesso.");
    }
}
