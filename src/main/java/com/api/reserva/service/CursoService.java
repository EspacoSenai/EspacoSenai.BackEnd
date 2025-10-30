package com.api.reserva.service;

import com.api.reserva.dto.CursoDTO;
import com.api.reserva.entity.Curso;
import com.api.reserva.entity.Turma;
import com.api.reserva.enums.NotificacaoTipo;
import com.api.reserva.exception.EntidadeJaAssociadaException;
import com.api.reserva.exception.EntidadeJaExistente;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private NotificacaoService notificacaoService;

    public List<CursoDTO> buscar() {
        return cursoRepository.findAll()
                .stream()
                .map(CursoDTO::new)
                .toList();
    }

    public CursoDTO buscar(Long id) {
        return cursoRepository.findById(id)
                .map(CursoDTO::new)
                .orElseThrow(SemResultadosException::new);
    }

    public void salvar(CursoDTO cursoDTO) {
        if(cursoRepository.existsByNome(cursoDTO.getNome())) {
            throw new EntidadeJaExistente("Curso com nome " + cursoDTO.getNome());
        }

        Curso curso = new Curso();
        curso.setNome(cursoDTO.getNome());
        curso.setDescricao(cursoDTO.getDescricao());
        cursoRepository.save(curso);
    }

    public void atualizar(CursoDTO cursoDTO, Long id) {
        if(cursoRepository.existsByNomeAndIdNot(cursoDTO.getNome(), id)) {
            throw new EntidadeJaExistente("Curso com nome " + cursoDTO.getNome());
        }

        boolean mudou;

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(SemResultadosException::new);

        curso.setNome(cursoDTO.getNome());
        curso.setDescricao(cursoDTO.getDescricao());
        cursoRepository.save(curso);

        for (Turma turma : curso.getTurmas()) {
            if (turma.getProfessor() != null) {
                notificacaoService.novaNotificacao(
                        turma.getProfessor(),
                        NotificacaoTipo.ALTERACOES,
                        "Curso atualizado",
                        "O curso " + curso.getNome() + " foi atualizado."
                );
            }
        }
    }

    public void deletar(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(SemResultadosException::new);

        List<LocalDate> datasFim = curso.getTurmas().stream()
                .map(Turma::getDataTermino)
                .toList();

        boolean possuiTurmasAtivas = datasFim.stream()
                .anyMatch(dataFim -> dataFim.isAfter(LocalDate.now()));

        if (!curso.getTurmas().isEmpty() && possuiTurmasAtivas) {
            throw new EntidadeJaAssociadaException("Curso possui turmas ativas.");
        }

        cursoRepository.delete(curso);
    }
}
