package com.api.reserva.service;

import com.api.reserva.dto.TurmaDTO;
import com.api.reserva.dto.TurmaReferenciaDTO;
import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.Role;
import com.api.reserva.entity.Turma;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.EntidadeJaAssociadaException;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.TurmaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.CodigoUtil;
import com.api.reserva.util.MetodosAuth;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TurmaService {
    @Autowired
    TurmaRepository turmaRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private NotificacaoService notificacaoService;
    @Autowired
    private ReservaRepository reservaRepository;

    public List<TurmaReferenciaDTO> buscar() {
        List<Turma> turmas = turmaRepository.findAll();
        if (turmas.isEmpty()) {
            throw new SemResultadosException();
        }
        return turmas.stream().map(TurmaReferenciaDTO::new).toList();
    }

    public TurmaReferenciaDTO buscar(Long id) {
        Turma turma = turmaRepository.findById(id).orElseThrow(SemResultadosException::new);
        return new TurmaReferenciaDTO(turma);
    }

    public void salvar(TurmaDTO turmaDTO, Authentication authentication) {
        if (ValidacaoDatasEHorarios.validarDatas(turmaDTO.getDataInicio(), turmaDTO.getDataTermino())) {
            Set<String> roles = MetodosAuth.extrairRole(authentication);
            Turma turma = new Turma(
                    turmaDTO.getNome(),
                    turmaDTO.getModalidade(),
                    turmaDTO.getCurso(),
                    turmaDTO.getDataInicio(),
                    turmaDTO.getDataTermino(),
                    turmaDTO.getCapacidade()
            );

            if(turmaDTO.getEstudantesIds().size() > turmaDTO.getCapacidade()) {
                throw new SemPermissaoException("Número de estudantes excede a capacidade máxima da turma.");
            }

            if (roles.contains("SCOPE_ADMIN")) {
                if (turmaDTO.getProfessorId() != null) {
                    Usuario professor = usuarioRepository.findById(turmaDTO.getProfessorId()).orElseThrow(() ->
                            new SemResultadosException("Professor"));
                    if (!professor.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.PROFESSOR)) {
                        throw new SemPermissaoException("O usuário designado não é um professor.");
                    }
                    turma.setProfessor(professor);
                }
            } else if (roles.contains("SCOPE_PROFESSOR")) {
                Long professorId = MetodosAuth.extrairId(authentication);
                Usuario professor = usuarioRepository.findById(professorId).orElseThrow(() -> new SemResultadosException("Professor"));
                if (!professor.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.PROFESSOR)) {
                    throw new SemPermissaoException("O usuário designado não é um professor.");
                }
                turma.setProfessor(professor);
            }

            turma.setCodigoAcesso(CodigoUtil.gerarCodigo(6));
//            // Set estudantes if provided
//            if (turmaDTO.getEstudantesIds() != null && !turmaDTO.getEstudantesIds().isEmpty()) {
//                Set<Usuario> estudantes = usuarioRepository.findAllById(turmaDTO.getEstudantesIds())
//                        .stream()
//                        .filter(u -> u.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ESTUDANTE))
//                        .collect(Collectors.toSet());
//                turma.setEstudantes(estudantes);
//            }
//
//
//            turmaRepository.save(turma);
//
////            notificacaoService.novaNotificacao(
////                    professor,
////                    NotificacaoTipo.TURMA,
////                    "Designado como professor",
////                    "Você foi designado como professor da nova turma " + turma.getNome() + "."
////            );
//
//            if (turmaDTO.getEstudantesIds() != null && !turmaDTO.getEstudantesIds().isEmpty()) {
//                Set<Usuario> estudantes = turma.getEstudantes();
//                for (Usuario                                                                                                                                                           estudante : estudantes) {
//                    notificacaoService.novaNotificacao(
//                            estudante,
//                            NotificacaoTipo.TURMA,
//                            "Adicionado à turma",
//                            "Você foi adicionado à nova turma " + turma.getNome() + "."
//                    );
//                }
//            }
            turmaRepository.save(turma);
        }
    }

    @Transactional
    public void atualizar(Authentication authentication, Long id, TurmaDTO turmaDTO) {
        if (ValidacaoDatasEHorarios.validarDatas(turmaDTO.getDataInicio(), turmaDTO.getDataTermino())) {

            Turma turma = turmaRepository.findById(id).orElseThrow(SemResultadosException::new);

            Set<String> roles = MetodosAuth.extrairRole(authentication);

            if (!roles.contains("SCOPE_ADMIN") && !turma.getProfessor().getId().equals(MetodosAuth.extrairId(authentication))) {
                throw new SemPermissaoException("Permissão negada para atualizar a turma.");
            }

            // Verificar se a nova capacidade máxima é menor que o número atual de estudantes
            if((turmaDTO.getCapacidade() != turma.getCapacidade()) &&
            (turmaDTO.getCapacidade() < turma.getEstudantes().size())) {
                throw new SemPermissaoException("Número de estudantes excede a nova capacidade máxima da turma.");
            }

            boolean mudou = false;

            // capturar dataTermino antiga antes de alterar
            LocalDate terminoAntigo = turma.getDataTermino();
            LocalDate terminoNovo = turmaDTO.getDataTermino();

            if (!turma.getNome().equals(turmaDTO.getNome())) {
                turma.setNome(turmaDTO.getNome());
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

            if (!turma.getDataTermino().equals(terminoNovo)) {
                turma.setDataTermino(terminoNovo);
                mudou = true;

                // Se a nova data de término é anterior à antiga, cancelar reservas dos estudantes cuja data é posterior
                if (terminoNovo.isBefore(terminoAntigo)) {
                    // coletar ids dos estudantes
                    Set<Long> estudantesIds = turma.getEstudantes()
                            .stream()
                            .map(Usuario::getId)
                            .collect(Collectors.toSet());

                    if (!estudantesIds.isEmpty()) {
                        Set<StatusReserva> statusesAtivos = EnumSet.of(StatusReserva.APROVADA, StatusReserva.ACONTECENDO, StatusReserva.PENDENTE);
                        Set<Reserva> reservasAfetadas = reservaRepository.findAllByHost_IdInAndDataGreaterThanAndStatusReservaIn(estudantesIds, terminoNovo, statusesAtivos);

                        for (Reserva reserva : reservasAfetadas) {
                            reserva.setStatusReserva(StatusReserva.CANCELADA);
                            reservaRepository.save(reserva);

//                            // notificar o host da reserva
                            notificacaoService.novaNotificacao(
                                    reserva.getHost(),
                                    "Reserva cancelada",
                                    "Sua reserva para " + reserva.getData() + " foi cancelada porque a turma " + turma.getNome() + " teve a data de término alterada."
                            );
                        }
                    }
                }
            }

            if (mudou) {
                // notificações detalhadas removidas; mantemos apenas atualização da turma
            }

            turmaRepository.save(turma);
        }
    }

    public void adicionarEstudantes(Long turmaId, List<Long> estudanteIds, Authentication authentication) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(SemResultadosException::new);

        Set<String> roles = MetodosAuth.extrairRole(authentication);

        if(!roles.contains("SCOPE_ADMIN") && !turma.getProfessor().getId().equals(MetodosAuth.extrairId(authentication))) {
            throw new SemPermissaoException("Você não é professor dessa turma.");
        }

        Integer estudantesTotais = turma.getEstudantes().size() + estudanteIds.size();
        if(estudantesTotais > turma.getCapacidade()) {
            throw new SemPermissaoException("Adicionar esses estudantes excede a capacidade máxima da turma.");
        }

        Set<Usuario> estudantes = usuarioRepository.findAllById(estudanteIds)
                .stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ESTUDANTE))
                .collect(Collectors.toSet());
        turma.getEstudantes().addAll(estudantes);
        turmaRepository.save(turma);
        // notificações removidas
    }

    public void removerEstudantes(Long turmaId, List<Long> estudanteIds, Authentication authentication) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(SemResultadosException::new);

        Set<String> roles = MetodosAuth.extrairRole(authentication);

        if(!roles.contains("SCOPE_ADMIN") && !turma.getProfessor().getId().equals(MetodosAuth.extrairId(authentication))) {
            throw new SemPermissaoException("Você não é professor dessa turma.");
        }

        Set<Usuario> estudantes = usuarioRepository.findAllById(estudanteIds)
                .stream()
                .collect(Collectors.toSet());
        turma.getEstudantes().removeAll(estudantes);
        turmaRepository.save(turma);
        // notificações removidas
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
        // notificações removidas
    }

    public void ingressarPorCodigo(String codigoAcesso, Authentication authentication) {
        Turma turma = turmaRepository.findByCodigoAcesso(codigoAcesso).orElseThrow(() ->
                new SemResultadosException("Código de acesso inválido."));
        Long estudanteId = MetodosAuth.extrairId(authentication);
        Usuario estudante = usuarioRepository.findById(estudanteId).orElseThrow(() ->
                new SemResultadosException("Estudante"));

        boolean estaNaTurma = turma.getEstudantes().contains(estudante);

        if(estaNaTurma) {
            throw new EntidadeJaAssociadaException("Você já está na turma.");
        }

        turma.getEstudantes().add(estudante);
        turmaRepository.save(turma);
        // notificações removidas
    }

    public void gerarNovoCodigo(Long turmaId, Authentication authentication) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() ->
                new SemResultadosException("Turma"));

        // validação de permissão mantida: professor dono ou ADMIN
        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);

        if(!MetodosAuth.extrairRole(authentication).contains("SCOPE_ADMIN") &&
                !turma.getProfessor().getId().equals(usuarioLogadoId)) {
            throw new SemPermissaoException("Você não é professor dessa turma.");
        }

        String novoCodigo = CodigoUtil.gerarCodigo(6);
        turma.setCodigoAcesso(novoCodigo);
        turmaRepository.save(turma);
        // notificações removidas
    }



    @Transactional
    public void deletar(Long id, Authentication authentication) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new SemResultadosException("Turma"));

        Set<String> roles = MetodosAuth.extrairRole(authentication);

        // Verifica se o usuário autenticado é o professor da turma
        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);
        if (!roles.contains("SCOPE_ADMIN") && !turma.getProfessor().getId().equals(usuarioLogadoId)) {
            throw new SemPermissaoException("Permissão negada para deletar a turma.");
        }

        Set<Usuario> estudantesDaTurma = turma.getEstudantes();

        // statuses considerados ativos para cancelamento
        Set<StatusReserva> statusesAtivos = EnumSet.of(StatusReserva.APROVADA, StatusReserva.ACONTECENDO, StatusReserva.PENDENTE);

        for (Usuario estudante : estudantesDaTurma) {
            // buscar todas as turmas do estudante (exceto a que será deletada)
            List<Turma> turmasDoEstudante = turmaRepository.findAllByEstudantes_Id(estudante.getId())
                    .stream()
                    .filter(t -> !t.getId().equals(turma.getId()))
                    .collect(Collectors.toList());

            boolean possuiOutraComTerminoMaior = turmasDoEstudante.stream()
                    .anyMatch(t -> t.getDataTermino() != null && t.getDataTermino().isAfter(turma.getDataTermino()));

            if (!possuiOutraComTerminoMaior) {
                // cancelar/resolver reservas onde o estudante é host
                Set<Reserva> reservasHost = reservaRepository.findAllByHost_Id(estudante.getId());
                if (reservasHost != null) {
                    for (Reserva reserva : reservasHost) {
                        if (reserva.getStatusReserva() != null && statusesAtivos.contains(reserva.getStatusReserva())) {
                            reserva.setStatusReserva(StatusReserva.CANCELADA);
                            reservaRepository.save(reserva);

//                            // notificar o host da reserva
//                            notificacaoService.novaNotificacao(
//                                    reserva.getHost(),
//                                    NotificacaoTipo.ALTERACOES,
//                                    "Reserva cancelada",
//                                    "Sua reserva para " + reserva.getData() + " foi cancelada porque a turma " + turma.getNome() + " foi excluída."
//                            );
//
//                            // notificar todos os membros/participantes da reserva sobre o cancelamento
//                            if (reserva.getMembros() != null) {
//                                for (Usuario membro : reserva.getMembros()) {
//                                    notificacaoService.novaNotificacao(
//                                            membro,
//                                            NotificacaoTipo.ALTERACOES,
//                                            "Reserva cancelada",
//                                            "A reserva para " + reserva.getData() + " foi cancelada porque a turma " + turma.getNome() + " foi excluída."
//                                    );
//                                }
//                            }
                        }
                    }
                }

                // tratar reservas onde o estudante é membro: apenas removê-lo da reserva em vez de cancelar a reserva inteira
                Set<Reserva> reservasMembro = reservaRepository.findAllByMembros_IdAndStatusReservaIn(estudante.getId(), statusesAtivos);
                if (reservasMembro != null) {
                    for (Reserva reserva : reservasMembro) {
                        if (reserva.getMembros() != null && reserva.getMembros().removeIf(u -> u.getId().equals(estudante.getId()))) {
                            reservaRepository.save(reserva);
                        }

                        // notificar o estudante que ele foi removido da reserva
                        notificacaoService.novaNotificacao(
                                estudante,
                                "Removido da reserva",
                                "Você foi removido da reserva para " + reserva.getData() + " porque a turma " + turma.getNome() + " foi excluída."
                        );

                        // opcional: notificar o host que um participante foi removido (mantido para transparência)
                        if (reserva.getHost() != null) {
                            notificacaoService.novaNotificacao(
                                    reserva.getHost(),
                                    "Participante removido",
                                    "O participante " + estudante.getNome() + " foi removido da sua reserva em " + reserva.getData() + " devido à exclusão da turma " + turma.getNome() + "."
                            );
                        }
                    }
                }
            }
        }

        // limpar associações e remover a turma
        turma.getEstudantes().clear();
        turmaRepository.delete(turma);
    }

    public Set<TurmaReferenciaDTO> minhasTurmas(Authentication authentication) {
        Long usuarioId = MetodosAuth.extrairId(authentication);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new SemResultadosException("Usuário"));

        boolean isProfessor = usuario.getRoles().stream()
                .anyMatch(r -> r.getRoleNome() == Role.Values.PROFESSOR);

        Set<TurmaReferenciaDTO> turmas = new HashSet<>();

        if (isProfessor) {
            // Se for professor, buscar turmas onde ele é o professor
            turmas.addAll(turmaRepository.findAllByProfessor_Id(usuarioId).stream()
                    .map(TurmaReferenciaDTO::new)
                    .collect(Collectors.toSet()));
        } else {
            // Se for estudante, buscar turmas onde ele é estudante
            turmas.addAll(turmaRepository.findAllByEstudantes_Id(usuarioId).stream()
                    .map(TurmaReferenciaDTO::new)
                    .collect(Collectors.toSet()));
        }

        if (turmas.isEmpty()) {
            throw new SemResultadosException();
        }

        return turmas;
    }
}
