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

    @GetMapping("/listar")
    public ResponseEntity<List<ReservaReferenciaDTO>> listar() {
        return ResponseEntity.ok(reservaService.listar());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<ReservaReferenciaDTO> listar (Long id) {
        return ResponseEntity.ok(reservaService.listar(id));
    }

    @GetMapping("/salvar")
    public ResponseEntity<Object> salvar(ReservaDTO reservaDTO) {
        reservaService.salvar(reservaDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Pedido de reserva criado. Aguarde aprovação.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody ReservaDTO reservaDTO) {
        reservaService.atualizar(id, reservaDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Reserva atualizada com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id) {
        reservaService.excluir(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Reserva excluída com sucesso.");
    }

}
