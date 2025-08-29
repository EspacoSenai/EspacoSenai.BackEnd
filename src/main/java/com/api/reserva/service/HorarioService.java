package com.api.reserva.service;

import com.api.reserva.dto.HorarioDTO;
import com.api.reserva.entity.Horario;
import com.api.reserva.exception.HorarioInvalidoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class HorarioService {
    @Autowired
    private HorarioRepository horarioRepository;

    public List<HorarioDTO> buscar() {
        return horarioRepository.findAll().stream()
                .map(HorarioDTO::new).toList();
    }

    public HorarioDTO buscar(Long id) {
        return new HorarioDTO(horarioRepository.findById(id).orElseThrow(SemResultadosException::new));
    }

    public void salvar(HorarioDTO horarioDTO) {

        if(horarioDTO.getHoraInicio().isAfter(horarioDTO.getHoraFim()) ||
                Objects.equals(horarioDTO.getHoraInicio(), horarioDTO.getHoraFim())){
            throw new HorarioInvalidoException();
        }

        horarioRepository.save(new Horario(horarioDTO));
    }

    public void atualizar(Long id, HorarioDTO horarioDTO) {
        Horario horario = horarioRepository.findById(id).orElseThrow(() -> new SemResultadosException("atualização."));

        if(horarioDTO.getHoraInicio().isAfter(horarioDTO.getHoraFim()) ||
                Objects.equals(horarioDTO.getHoraInicio(), horarioDTO.getHoraFim())){
            throw new HorarioInvalidoException();
        }

        if(horario.getHoraInicio() != horarioDTO.getHoraInicio()) {
            horario.setHoraInicio(horarioDTO.getHoraInicio());
        }

        if(horario.getHoraFim() != horarioDTO.getHoraFim()) {
            horario.setHoraFim(horarioDTO.getHoraFim());
        }

        horarioRepository.save(horario);
    }

    public void deletar (Long id) {
        Horario horario = horarioRepository.findById(id).orElseThrow(() -> new SemResultadosException("exclusão"));
        horarioRepository.delete(horario);
    }
}
