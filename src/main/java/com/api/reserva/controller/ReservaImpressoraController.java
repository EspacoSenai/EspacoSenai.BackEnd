package com.api.reserva.controller;

import com.api.reserva.dto.*;
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
@RequestMapping("/reserva-impressora")
public class ReservaImpressoraController {

    @Autowired
    private ReservaImpressoraService reservaImpressoraService;

    @GetMapping
    public ResponseEntity<List<ReservaImpressoraReferenciaDTO>> buscarTodas() {
        List<ReservaImpressoraReferenciaDTO> reservas = reservaImpressoraService.buscar();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR')")
    public ResponseEntity<ReservaImpressoraReferenciaDTO> buscarPorId(@PathVariable Long id) {
        ReservaImpressoraReferenciaDTO reserva = reservaImpressoraService.buscar(id);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR', 'SCOPE_PROFESSOR', 'SCOPE_ESTUDANTE')")
    public ResponseEntity<Void> salvar(@RequestBody @Valid ReservaImpressoraReferenciaDTO reserva, Authentication authentication) {
        reservaImpressoraService.salvar(reserva, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/pin")
    public ResponseEntity<Object> liberarMamarquinaminha(@RequestBody Pin pin){
        RespostaPin resposta = reservaImpressoraService.atualizarStatusPeloPin(pin);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/temperatura")
    public void atualizarTemperatura(@RequestBody Temperatura temperatura){
        reservaImpressoraService.atualizarTemperatura(temperatura);
    }
    @GetMapping("/desligar")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> desligarMaquina(){
       reservaImpressoraService.desligarMaquina();
       return ResponseEntity.noContent().build();
    }

    @PostMapping("/achar-usuario/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UsuarioReferenciaDTO> acharUsuario (@PathVariable Long id){
        UsuarioReferenciaDTO reserva = reservaImpressoraService.acharPorReserva(id);
        return ResponseEntity.ok(reserva);
    }

}