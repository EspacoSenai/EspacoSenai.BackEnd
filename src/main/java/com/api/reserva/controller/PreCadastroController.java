package com.api.reserva.controller;

import com.api.reserva.dto.PreCadastroDTO;
import com.api.reserva.service.PreCadastroService;
import com.api.reserva.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("precadastro")
public class PreCadastroController {

    private final PreCadastroService preCadastroService;

    public PreCadastroController(PreCadastroService preCadastroService) {
        this.preCadastroService = preCadastroService;
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")
    @GetMapping("/buscar")
    public List<PreCadastroDTO> buscar() {
        return preCadastroService.buscar();
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PostMapping("/salvar")
    public ResponseEntity<Object> salvar(@Valid @RequestBody PreCadastroDTO preCadastroDTO) {
        preCadastroService.salvar(preCadastroDTO);
        return ResponseBuilder.respostaSimples(HttpStatus.CREATED, "Estudante pré-cadastrado com sucesso.");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_PROFESSOR')")
    @PostMapping("/planilha")
    public ResponseEntity<Object> salvar(@RequestParam MultipartFile planilha) {
        preCadastroService.salvarEstudantesPlanilha(planilha);
        return ResponseBuilder.respostaSimples(HttpStatus.OK, "Estudantes pré-cadastros com sucesso.");
    }

//    @GetMapping("/verificar/{identificador}")
//    public ResponseEntity<Object> verificarElegibilidade(@PathVariable String identificador) {
//        boolean elegivel = preCadastroService.verificarElegibilidade(identificador);
//        if(elegivel) {
//            return ResponseBuilder.respostaSimples(HttpStatus.OK, "Elegível. cadastro.");
//        } else {
//            return ResponseBuilder.respostaSimples(HttpStatus.OK, "Não elegível. Fale com um professor ou administrador.");
//        }
//    }
}
