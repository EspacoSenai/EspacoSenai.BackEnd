package com.api.reserva.controller;

import com.api.reserva.dto.CategoriaDTO;
import com.api.reserva.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    CategoriaService service;

    @GetMapping("/listar")
    public ResponseEntity<List<CategoriaDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<CategoriaDTO> listar(@PathVariable Long id) {
        return ResponseEntity.ok(service.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<CategoriaDTO> salvar(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        return ResponseEntity.ok(service.salvar(categoriaDTO));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@Valid @PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        return ResponseEntity.ok(service.atualizar(id, categoriaDTO));
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok().build();
    }
}
