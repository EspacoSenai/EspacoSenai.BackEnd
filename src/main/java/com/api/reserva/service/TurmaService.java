package com.api.reserva.service;

import com.api.reserva.dto.TurmaDTO;
import com.api.reserva.entity.Turma;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioRole;
import com.api.reserva.exception.DadoDuplicadoException;
import com.api.reserva.exception.EntidadeJaAssociadaException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioInvalidoException;
import com.api.reserva.repository.TurmaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.ValidacaoDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TurmaService {
    @Autowired
    TurmaRepository turmaRepository;
    @Autowired
    UsuarioRepository usuarioRepository;

    public List<TurmaDTO> buscar() {
        List<Turma> turmas = turmaRepository.findAll();
        if (turmas.isEmpty()) {
            throw new SemResultadosException();
        }
        return turmas.stream().map(TurmaDTO::new).toList();
    }

    public TurmaDTO buscar(Long id) {
        Turma turma = turmaRepository.findById(id).orElseThrow(SemResultadosException::new);
        return new TurmaDTO(turma);
    }

    public void salvar(TurmaDTO turmaDTO) {
        if (ValidacaoDatas.validarDatas(turmaDTO.getDataInicio(), turmaDTO.getDataTermino())) {
            Turma turma = new Turma(turmaDTO);
            turmaRepository.save(turma);
        }
    }


    public void atualizar(Long id, TurmaDTO turmaDTO) {
        if (ValidacaoDatas.validarDatas(turmaDTO.getDataInicio(), turmaDTO.getDataTermino())) {
            Turma turma = turmaRepository.findById(id).orElseThrow(SemResultadosException::new);
            turma.setNome(turmaDTO.getNome());
            turma.setDataInicio(turmaDTO.getDataInicio());
            turma.setDataTermino(turma.getDataTermino());
            turmaRepository.save(turma);
        }
    }

    public void excluir(Long id) {
        Turma turma = turmaRepository.findById(id).orElseThrow(SemResultadosException::new);
        turmaRepository.delete(turma);
    }

    public void associarEstudante(Long turmaId, Long estudanteId) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new SemResultadosException("Turma"));
        Usuario estudante = usuarioRepository.findById(estudanteId).orElseThrow(() -> new SemResultadosException("Estudante"));
        if(estudante.getRole() != UsuarioRole.ESTUDANTE) {
            throw new UsuarioInvalidoException("Apenas Estudantes podem ser atribuídos às turmas.");
        }
        if (turma.getEstudantes().add(estudante)) {
            turmaRepository.save(turma);
        } else {
            throw new EntidadeJaAssociadaException("O estudante já pertence à turma.");
        }
    }

    public void associarEstudantes(Long turmaId, Long estudanteId) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new SemResultadosException("Turma"));

    }

    public void desassociarEstudante(Long turmaId, Long estudanteId) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new SemResultadosException("Turma"));
        Usuario estudante = usuarioRepository.findById(estudanteId).orElseThrow(() -> new SemResultadosException("Estudante"));
        turma.getEstudantes().remove(estudante);
        turmaRepository.save(turma);
    }
}
