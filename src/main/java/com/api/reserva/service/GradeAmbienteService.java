package com.api.reserva.service;

import com.api.reserva.dto.GradeAmbienteDTO;
import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.GradeAmbiente;
import com.api.reserva.enums.Agendamento;
import com.api.reserva.exception.DadoInvalidoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.AmbienteRepository;
import com.api.reserva.repository.GradeAmbienteRepository;
import com.api.reserva.repository.HorarioRepository;
import com.api.reserva.repository.PeriodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class GradeAmbienteService {
    @Autowired
    public GradeAmbienteRepository gradeAmbienteRepository;
    @Autowired
    public AmbienteRepository ambienteRepository;
    @Autowired
    public PeriodoRepository periodoRepository;
    @Autowired
    public HorarioRepository horarioRepository;

    public List<GradeAmbienteDTO> listar() {
        return gradeAmbienteRepository.findAll().stream()
                .map(GradeAmbienteDTO::new)
                .toList();
    }

    public GradeAmbienteDTO listar(Long id) {
        return new GradeAmbienteDTO(gradeAmbienteRepository.findById(id).orElseThrow(SecurityException::new));
    }

    @Transactional
    public void salvar(GradeAmbienteDTO gradeAmbienteDTO) {
        GradeAmbiente gradeAmbiente = new GradeAmbiente();

        gradeAmbiente.setAmbiente(ambienteRepository.findById(gradeAmbienteDTO.getId()).orElseThrow(() ->
                new SemResultadosException("vinculação de Ambiente.")));

        if (Objects.equals(gradeAmbienteDTO.getAgendamento(), Agendamento.PERIODO)
                && gradeAmbienteDTO.getIdHorario() == null) {
            gradeAmbiente.setAgendamento(Agendamento.PERIODO);
            gradeAmbiente.setPeriodo(periodoRepository.findById(gradeAmbienteDTO.getId()).orElseThrow(()
                    -> new SemResultadosException("vinculação de Periodo.")));
        } else if (Objects.equals(gradeAmbienteDTO.getAgendamento(), Agendamento.HORARIO)
                && gradeAmbienteDTO.getIdPeriodo() == null) {
            gradeAmbiente.setAgendamento(Agendamento.HORARIO);
            gradeAmbiente.setHorario(horarioRepository.findById(gradeAmbienteDTO.getId()).orElseThrow(()
                    -> new SemResultadosException("vinculação de Horário.")));
        } else {
            throw new DadoInvalidoException("Escolha um tipo de agendamento e preencha somente seu campo.");
        }

        gradeAmbiente.setDisponibilidade(gradeAmbienteDTO.getDisponibilidade());
        gradeAmbienteRepository.save(gradeAmbiente);
    }

    public void atualizar(Long id, GradeAmbienteDTO gradeAmbienteDTO) {
        GradeAmbiente gradeAmbiente = gradeAmbienteRepository.findById(id).orElseThrow(()
                -> new SemResultadosException("atualização"));

        gradeAmbiente.setAmbiente(ambienteRepository.findById(gradeAmbienteDTO.getId()).orElseThrow(() ->
                new SemResultadosException("vinculação de Ambiente.")));

        if (Objects.equals(gradeAmbienteDTO.getAgendamento(), Agendamento.PERIODO)
                && gradeAmbienteDTO.getIdHorario() == null) {
            gradeAmbiente.setAgendamento(Agendamento.PERIODO);
            gradeAmbiente.setPeriodo(periodoRepository.findById(gradeAmbienteDTO.getId()).orElseThrow(()
                    -> new SemResultadosException("vinculação de Periodo.")));
        } else if (Objects.equals(gradeAmbienteDTO.getAgendamento(), Agendamento.HORARIO)
                && gradeAmbienteDTO.getIdPeriodo() == null) {
            gradeAmbiente.setAgendamento(Agendamento.HORARIO);
            gradeAmbiente.setHorario(horarioRepository.findById(gradeAmbienteDTO.getId()).orElseThrow(()
                    -> new SemResultadosException("vinculação de Horário.")));
        } else {
            throw new DadoInvalidoException("Escolha um tipo de agendamento e preencha somente seu campo.");
        }

        gradeAmbiente.setDisponibilidade(gradeAmbienteDTO.getDisponibilidade());
        gradeAmbienteRepository.save(gradeAmbiente);
    }

    @Transactional
    public void excluir(Long id) {
        Ambiente ambiente = ambienteRepository.findById(id).orElseThrow(() -> new SemResultadosException("exclusão"));
        ambienteRepository.delete(ambiente);
    }
}
