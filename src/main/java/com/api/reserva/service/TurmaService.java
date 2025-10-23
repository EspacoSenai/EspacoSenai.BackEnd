package com.api.reserva.service;

import com.api.reserva.dto.TurmaDTO;
import com.api.reserva.entity.Curso;
import com.api.reserva.entity.Role;
import com.api.reserva.entity.Turma;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.NotificacaoTipo;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.CursoRepository;
import com.api.reserva.repository.TurmaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.CodigoUtil;
import com.api.reserva.util.MetodosAuth;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    @Autowired
    CursoRepository cursoRepository;
    @Autowired
    private NotificacaoService notificacaoService;

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

    public void salvar(TurmaDTO turmaDTO, Authentication authentication) {
        if (ValidacaoDatasEHorarios.validarDatas(turmaDTO.getDataInicio(), turmaDTO.getDataTermino())) {
            Set<String> roles = MetodosAuth.extrairRole(authentication);
            Usuario professor = null;

            Turma turma = new Turma(
                    turmaDTO.getNome(),
                    turmaDTO.getModalidade(),
                    turmaDTO.getDataInicio(),
                    turmaDTO.getDataTermino()
            );

            if (roles.contains("SCOPE_ADMIN")) {
                if (turmaDTO.getProfessorId() != null) {
                    professor = usuarioRepository.findById(turmaDTO.getProfessorId()).orElseThrow(SemResultadosException::new);
                    if (!professor.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.PROFESSOR)) {
                        throw new SemResultadosException();
                    }
                }
            } else if (roles.contains("SCOPE_PROFESSOR")) {
                Long professorId = MetodosAuth.extrairId(authentication);
                professor = usuarioRepository.findById(professorId).orElseThrow(SemResultadosException::new);
            }

            turma.setProfessor(professor);

            Curso curso = cursoRepository.findById(turmaDTO.getCursoId()).orElseThrow(SemResultadosException::new);
            turma.setCurso(curso);

            // Set estudantes if provided
            if (turmaDTO.getEstudantesIds() != null && !turmaDTO.getEstudantesIds().isEmpty()) {
                Set<Usuario> estudantes = usuarioRepository.findAllById(turmaDTO.getEstudantesIds())
                        .stream()
                        .filter(u -> u.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ESTUDANTE))
                        .collect(Collectors.toSet());
                turma.setEstudantes(estudantes);
            }

            turma.setCodigoAcesso(CodigoUtil.gerarCodigo(6));

            turmaRepository.save(turma);

            notificacaoService.novaNotificacao(
                    professor,
                    NotificacaoTipo.TURMA,
                    "Designado como professor",
                    "Você foi designado como professor da nova turma " + turma.getNome() + "."
            );

            if (turmaDTO.getEstudantesIds() != null && !turmaDTO.getEstudantesIds().isEmpty()) {
                Set<Usuario> estudantes = turma.getEstudantes();
                for (Usuario estudante : estudantes) {
                    notificacaoService.novaNotificacao(
                            estudante,
                            NotificacaoTipo.TURMA,
                            "Adicionado à turma",
                            "Você foi adicionado à nova turma " + turma.getNome() + "."
                    );
                }
            }
        }
    }

    public void atualizar(Long id, TurmaDTO turmaDTO) {
        if (ValidacaoDatasEHorarios.validarDatas(turmaDTO.getDataInicio(), turmaDTO.getDataTermino())) {
            Turma turma = turmaRepository.findById(id).orElseThrow(SemResultadosException::new);
            boolean mudou = false;

            if (!turma.getNome().equals(turmaDTO.getNome())) {
                turma.setNome(turmaDTO.getNome());
                mudou = true;
            }

            if (!turma.getCurso().getId().equals(turmaDTO.getCursoId())) {
                Curso curso = cursoRepository.findById(turmaDTO.getCursoId()).orElseThrow(SemResultadosException::new);
                turma.setCurso(curso);
                mudou = true;
            }

            if (!turma.getModalidade().equals(turmaDTO.getModalidade())) {
                turma.setModalidade(turmaDTO.getModalidade());
                mudou = true;
            }

            if (!turma.getDataInicio().equals(turmaDTO.getDataInicio())) {
                turma.setDataInicio(turmaDTO.getDataInicio());
                mudou = true;
            }

            if (!turma.getDataTermino().equals(turmaDTO.getDataTermino())) {
                turma.setDataTermino(turmaDTO.getDataTermino());
                mudou = true;
            }

            if (mudou) {
                for (Usuario estudante : turma.getEstudantes()) {
                    notificacaoService.novaNotificacao(
                            estudante,
                            NotificacaoTipo.TURMA,
                            "Alteração na turma",
                            "Houve uma alteração na turma " + turma.getNome() + ". Verifique os detalhes."
                    );
                }
            }

            turmaRepository.save(turma);
        }
    }

    public void adicionarEstudantes(Long turmaId, List<Long> estudanteIds) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(SemResultadosException::new);
        Set<Usuario> estudantes = usuarioRepository.findAllById(estudanteIds)
                .stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ESTUDANTE))
                .collect(Collectors.toSet());
        turma.getEstudantes().addAll(estudantes);
        turmaRepository.save(turma);
        for (Usuario estudante : estudantes) {
            notificacaoService.novaNotificacao(
                    estudante,
                    NotificacaoTipo.ALTERACOES,
                    "Adicionado à turma",
                    "Você foi adicionado à turma " + turma.getNome() + "."
            );
        }
    }

    public void removerEstudantes(Long turmaId, List<Long> estudanteIds) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(SemResultadosException::new);
        Set<Usuario> estudantes = usuarioRepository.findAllById(estudanteIds)
                .stream()
                .collect(Collectors.toSet());
        turma.getEstudantes().removeAll(estudantes);
        turmaRepository.save(turma);
        for (Usuario estudante : estudantes) {
            notificacaoService.novaNotificacao(
                    estudante,
                    NotificacaoTipo.ALTERACOES,
                    "Removido da turma",
                    "Você foi removido da turma " + turma.getNome() + "."
            );
        }
    }

    public void atualizarProfessor(Long turmaId, Long professorId) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(SemResultadosException::new);
        Usuario professor = usuarioRepository.findById(professorId).orElseThrow(SemResultadosException::new);
        if (!professor.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.PROFESSOR)) {
            throw new SemResultadosException();
        }
        Usuario professorAntigo = turma.getProfessor();
        turma.setProfessor(professor);
        turmaRepository.save(turma);
        if (professorAntigo != null && !professorAntigo.equals(professor)) {
            notificacaoService.novaNotificacao(
                    professorAntigo,
                    NotificacaoTipo.ALTERACOES,
                    "Removido da turma",
                    "Você foi removido como professor da turma " + turma.getNome() + "."
            );
        }
        notificacaoService.novaNotificacao(
                professor,
                NotificacaoTipo.ALTERACOES,
                "Designado como professor",
                "Você foi designado como professor da turma " + turma.getNome() + "."
        );
    }

    public void ingressarPorCodigo(String codigoAcesso, Authentication authentication) {
        Turma turma = turmaRepository.findByCodigoAcesso(codigoAcesso).orElseThrow(SemResultadosException::new);
        Long estudanteId = MetodosAuth.extrairId(authentication);
        Usuario estudante = usuarioRepository.findById(estudanteId).orElseThrow(SemResultadosException::new);
        turma.getEstudantes().add(estudante);
        turmaRepository.save(turma);
        notificacaoService.novaNotificacao(
                estudante,
                NotificacaoTipo.TURMA,
                "Adicionado à turma",
                "Você foi adicionado à turma " + turma.getNome() + " via código de acesso."
        );
    }

    public void gerarNovoCodigo(Long turmaId, Authentication authentication) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(SemResultadosException::new);

        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);

        if(!turma.getProfessor().getId().equals(usuarioLogadoId) ||
           !MetodosAuth.extrairRole(authentication).contains("SCOPE_ADMIN")) {
            throw new SemResultadosException("Permissão negada para gerar novo código de acesso.");
        }

        String novoCodigo = CodigoUtil.gerarCodigo(6);
        turma.setCodigoAcesso(novoCodigo);
        turmaRepository.save(turma);

        notificacaoService.novaNotificacao(
                turma.getProfessor(),
                NotificacaoTipo.TURMA,
                "Código de acesso atualizado",
                "O código de acesso da turma " + turma.getNome() + " foi atualizado."
        );
    }
}
