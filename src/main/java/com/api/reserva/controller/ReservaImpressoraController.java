package com.api.reserva.controller;

import com.api.reserva.dto.Pin;
import com.api.reserva.dto.ReservaImpressoraDTO;
import com.api.reserva.dto.TemperaturaDTO;
import com.api.reserva.service.ReservaImpressoraService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas-impressora")
public class ReservaImpressoraController {

    @Autowired
    private ReservaImpressoraService reservaImpressoraService;

    @GetMapping("/buscar")
    public ResponseEntity<List<ReservaImpressoraDTO>> buscarTodas() {
        List<ReservaImpressoraDTO> reservas = reservaImpressoraService.buscar();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/buscar/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR')")
    public ResponseEntity<ReservaImpressoraDTO> buscarPorId(@PathVariable Long id) {
        ReservaImpressoraDTO reserva = reservaImpressoraService.buscar(id);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    public ResponseEntity<Void> salvar(@RequestBody @Valid ReservaImpressoraDTO reserva, Authentication authentication) {
        reservaImpressoraService.salvar(reserva, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/liberar")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    public ResponseEntity<Object> atualizarStatusPeloPin(@RequestBody Pin pin){
        reservaImpressoraService.atualizarStatusPeloPin(pin);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Maquina liberada com sucesso.");
    }

    @PostMapping("/temperatura")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public void atualizarTemperatura(@RequestBody TemperaturaDTO temperaturaDTO){
        reservaImpressoraService.atualizarTemperatura(temperaturaDTO);
    }

    @GetMapping("/desligar")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> desligarMaquina(){
       reservaImpressoraService.desligarMaquina();
       return ResponseEntity.noContent().build();
    }

}
