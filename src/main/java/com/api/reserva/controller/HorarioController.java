package com.api.reserva.controller;

import com.api.reserva.dto.HorarioDTO;
import com.api.reserva.service.HorarioService;
import com.api.reserva.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("horario")
public class HorarioController {
    @Autowired
    private HorarioService horarioService;

    @GetMapping("/listar")
    public ResponseEntity<List<HorarioDTO>> listar() {
        return ResponseEntity.ok(horarioService.listar());
    }

    @GetMapping("listar/{id}")
    public ResponseEntity<HorarioDTO> listar (@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.listar(id));
    }

    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@RequestBody HorarioDTO horarioDTO) {
        horarioService.salvar(horarioDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Horário salvo com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody HorarioDTO horarioDTO) {
        horarioService.atualizar(id, horarioDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Horário atualizado com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id) {
        horarioService.excluir(id);
        return ResponseBuilder.respostaSimples(HttpStatus.NO_CONTENT, "Horário excluído com sucesso.");
    }
}
