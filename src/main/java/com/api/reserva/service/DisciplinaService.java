package com.api.reserva.service;

import com.api.reserva.dto.DisciplinaDTO;
import com.api.reserva.entity.Disciplina;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public List<DisciplinaDTO> listar() {
        return disciplinaRepository.findAll().stream()
                .map(DisciplinaDTO::new).toList();
    }

    public DisciplinaDTO listar(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new SemResultadosException("Disciplina não encontrada."));
        return new DisciplinaDTO(disciplina);
    }

    public void salvar(DisciplinaDTO dto) {
        disciplinaRepository.save(new Disciplina(dto));
    }

    public void atualizar(Long id, DisciplinaDTO dto) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new SemResultadosException("Disciplina para atualização não encontrada."));

        disciplina.setDisciplina(dto.getDisciplina());
        disciplinaRepository.save(disciplina);
    }

    public void excluir(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new SemResultadosException("Disciplina para exclusão não encontrada."));
        disciplinaRepository.delete(disciplina);
    }
}
