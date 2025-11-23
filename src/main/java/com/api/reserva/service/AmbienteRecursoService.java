package com.api.reserva.service;

import com.api.reserva.dto.AmbienteRecursoDTO;
import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Recurso;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.AmbienteRecursoRepository;
import com.api.reserva.repository.AmbienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmbienteRecursoService {

    private final AmbienteRecursoRepository ambienteRecursoRepository;
    private final AmbienteRepository ambienteRepository;

    public AmbienteRecursoService(AmbienteRecursoRepository ambienteRecursoRepository, AmbienteRepository ambienteRepository) {
        this.ambienteRecursoRepository = ambienteRecursoRepository;
        this.ambienteRepository = ambienteRepository;
    }

    public List<AmbienteRecursoDTO> buscar() {
        return ambienteRecursoRepository.findAll()
                .stream()
                .map(AmbienteRecursoDTO::new)
                .toList();
    }

    public AmbienteRecursoDTO buscar(Long id) {
        Recurso recurso = ambienteRecursoRepository.findById(id)
                .orElseThrow(() -> new SemResultadosException("Recurso do ambiente"));
        return new AmbienteRecursoDTO(recurso);
    }

    public void salvar(AmbienteRecursoDTO ambienteRecursoDTO) {
        Ambiente ambiente = ambienteRepository.findById(ambienteRecursoDTO.getAmbienteId())
                .orElseThrow(() -> new SemResultadosException("Ambiente"));
        Recurso recurso = new Recurso(
                ambiente,
                ambienteRecursoDTO.getNome(),
                ambienteRecursoDTO.getDescricao(),
                ambienteRecursoDTO.getDisponibilidade()
        );

        ambienteRecursoRepository.save(recurso);
    }

    public void deletar(Long id) {
        Recurso recurso = ambienteRecursoRepository.findById(id)
                .orElseThrow(() -> new SemResultadosException("Recurso do ambiente"));
        ambienteRecursoRepository.delete(recurso);
    }
}
