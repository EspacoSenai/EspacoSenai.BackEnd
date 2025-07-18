package com.api.reserva.controller;

import com.api.reserva.dto.CatalogoDTO;
import com.api.reserva.dto.CatalogoReferenciaDTO;
import com.api.reserva.service.CatalogoService;
import com.api.reserva.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("gradeambiente")
public class CatalogoController {
    @Autowired
    private CatalogoService catalogoService;

    @GetMapping("/listar")
    public ResponseEntity<List<CatalogoReferenciaDTO>> listar() {
        return ResponseEntity.ok(catalogoService.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<CatalogoReferenciaDTO> listar (@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@RequestBody CatalogoDTO catalogoDTO) {
        catalogoService.salvar(catalogoDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Grade de ambiente salva com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody CatalogoDTO catalogoDTO) {
        catalogoService.atualizar(id, catalogoDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Grade de ambiente atualizada com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir (@PathVariable Long id) {
        catalogoService.excluir(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Grade de ambiente excluída com sucesso.");
    }
}
