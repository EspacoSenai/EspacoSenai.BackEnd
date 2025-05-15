package com.api.reserva.service;

import com.api.reserva.dto.PeriodoDTO;
import com.api.reserva.entity.Periodo;
import com.api.reserva.exception.HorarioInvalidoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.PeriodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class PeriodoService {
    @Autowired
    PeriodoRepository repository;

    public List<PeriodoDTO> listar() {
        List<Periodo> periodos = repository.findAll();
        return periodos.stream()
                .map(PeriodoDTO::new)
                .toList();
    }

    public PeriodoDTO listar(Long id) {
        Periodo periodo = repository.findById(id).orElseThrow(SemResultadosException::new);
        return new PeriodoDTO(periodo);
    }

    public PeriodoDTO salvar(PeriodoDTO periodoDTO) {
        LocalTime inicioFormatado = periodoDTO.getHoraInicio();
        LocalTime terminoFormatado = periodoDTO.getHoraFim();

        if (inicioFormatado.isAfter(terminoFormatado) || inicioFormatado.equals(terminoFormatado)) {
            throw new HorarioInvalidoException();
        }

        Periodo periodo = new Periodo(periodoDTO.getPeriodoAmbiente(), inicioFormatado, terminoFormatado);
        return new PeriodoDTO(repository.save(periodo));
    }

    public PeriodoDTO atualizar(Long id, PeriodoDTO periodoDTO) {
        LocalTime inicio = periodoDTO.getHoraInicio();
        LocalTime termino = periodoDTO.getHoraFim();

        if (inicio.isAfter(termino) || inicio.equals(termino)) {
            throw new HorarioInvalidoException();
        }

        Periodo periodo = repository.findById(id).orElseThrow(SemResultadosException::new);

        if(periodoDTO.getPeriodoAmbiente() != null && periodo.getHoraInicio() != periodoDTO.getHoraInicio()) {
            periodo.setPeriodoAmbiente(periodoDTO.getPeriodoAmbiente());
        }

        if(periodoDTO.getHoraInicio() != null && periodo.getHoraInicio() != periodoDTO.getHoraInicio()) {
            periodo.setHoraInicio(periodoDTO.getHoraInicio());
        }

        if(periodoDTO.getHoraFim() != null && periodo.getHoraFim() != periodoDTO.getHoraFim()) {
            periodo.setHoraFim(periodoDTO.getHoraFim());
        }

        return new PeriodoDTO(repository.save(periodo));
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new SemResultadosException();
        }
        repository.deleteById(id);
    }
}
