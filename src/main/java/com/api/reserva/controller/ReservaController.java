package com.api.reserva.controller;

import com.api.reserva.dto.ReservaDTO;
import com.api.reserva.dto.ReservaReferenciaDTO;
import com.api.reserva.service.ReservaService;
import com.api.reserva.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reserva")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;


    @GetMapping("/buscar")
    public ResponseEntity<List<ReservaReferenciaDTO>> buscar() {
        return ResponseEntity.ok(reservaService.buscar());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ReservaReferenciaDTO> buscar (@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@RequestBody ReservaDTO reservaDTO) {
        reservaService.salvar(reservaDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Pedido de reserva criado. Aguarde aprovação.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody ReservaDTO reservaDTO) {
        reservaService.atualizar(id, reservaDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Reserva atualizada com sucesso.");
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        reservaService.deletar(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Reserva excluída com sucesso.");
    }

}
