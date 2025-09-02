package com.api.reserva.service;

import com.api.reserva.dto.TurmaDTO;
import com.api.reserva.entity.Role;
import com.api.reserva.entity.Turma;
import com.api.reserva.entity.Usuario;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.TurmaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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
        if (ValidacaoDatasEHorarios.validarDatas(turmaDTO.getDataInicio(), turmaDTO.getDataTermino())) {
            Turma turma = new Turma();
            turma.setNome(turmaDTO.getNome());
            turma.setCurso(turmaDTO.getCurso());
            turma.setModalidade(turmaDTO.getModalidade());
            turma.setDataInicio(turmaDTO.getDataInicio());
            turma.setDataTermino(turmaDTO.getDataTermino());
            turmaRepository.save(turma);
        }
    }

    public void atualizar(TurmaDTO turmaDTO, Long id) {
        if (ValidacaoDatasEHorarios.validarDatas(turmaDTO.getDataInicio(), turmaDTO.getDataTermino())) {
            Turma turma = turmaRepository.findById(id).orElseThrow(SemResultadosException::new);
            turma.setNome(turmaDTO.getNome());
            turma.setCurso(turmaDTO.getCurso());
            turma.setModalidade(turmaDTO.getModalidade());
            turma.setDataInicio(turmaDTO.getDataInicio());
            turma.setDataTermino(turmaDTO.getDataTermino());
            turmaRepository.save(turma);
        }
    }

    public void deletar(Long id) {
        Turma turma = turmaRepository.findById(id).orElseThrow(SemResultadosException::new);
        turmaRepository.delete(turma);
    }

    public void definirEstudantes(Long turmaId, List<Long> estudanteIds) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(SemResultadosException::new);

        Set<Usuario> estudantes = usuarioRepository.findAllById(estudanteIds)
                .stream()
                .filter(usuario -> usuario.getRoles()
                        .contains(Role.Values.ESTUDANTE))
                .collect(Collectors.toSet());

        turma.setEstudantes(estudantes);
        turmaRepository.save(turma);
    }

    public void definirProfessores(Long turmaId, List<Long> professoresIds) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(SemResultadosException::new);

        Set<Usuario> professores = usuarioRepository.findAllById(professoresIds)
                .stream()
                .filter(usuario -> usuario.getRoles()
                        .contains(Role.Values.PROFESSOR))
                .collect(Collectors.toSet());

        turma.setProfessores(professores);
        turmaRepository.save(turma);
    }

//    public void associarEstudante(Long turmaId, Long estudanteId) {
//        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new SemResultadosException("Turma"));
//        Usuario estudante = usuarioRepository.findById(estudanteId).orElseThrow(() -> new SemResultadosException("Estudante"));
//        if(!estudante.getRoles().contains(Role.Values.ESTUDANTE)) {
//            throw new UsuarioInvalidoException("Apenas Estudantes podem ser atribuídos às turmas.");
//        }
//        if (turma.getEstudantes().add(estudante)) {
//            turmaRepository.save(turma);
//        } else {
//            throw new EntidadeJaAssociadaException("O estudante já pertence à turma.");
//        }
//    }
//
//    public void associarProfessor(Long turmaId, Long professorId) {
//        Turma turma = turmaRepository.findById(turmaId)
//                .orElseThrow(() -> new SemResultadosException("Turma"));
//        Usuario professor = usuarioRepository.findById(professorId)
//                .orElseThrow(() -> new SemResultadosException("Estudante"));
//
//        if(!professor.getRoles().contains(Role.Values.PROFESSOR)) {
//            throw new UsuarioInvalidoException("Apenas professores podem gerenciar à turma.");
//        }
//
//        if (turma.getProfessores().add(professor)) {
//            turmaRepository.save(turma);
//        } else {
//            throw new EntidadeJaAssociadaException("O professor já pertence à turma.");
//        }
//    }
//
//
//
//    public void desassociarEstudante(Long turmaId, Long estudanteId) {
//        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new SemResultadosException("Turma"));
//        Usuario estudante = usuarioRepository.findById(estudanteId).orElseThrow(() -> new SemResultadosException("Estudante"));
//        turma.getEstudantes().remove(estudante);
//        turmaRepository.save(turma);
//    }
//
//    public void desassociar(Long turmaId, Long estudanteId) {
//        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new SemResultadosException("Turma"));
//        Usuario estudante = usuarioRepository.findById(estudanteId).orElseThrow(() -> new SemResultadosException("Estudante"));
//        turma.getEstudantes().remove(estudante);
//        turmaRepository.save(turma);
//    }
}
