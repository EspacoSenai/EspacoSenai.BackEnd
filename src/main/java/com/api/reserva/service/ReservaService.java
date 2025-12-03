package com.api.reserva.service;

import com.api.reserva.dto.ReservaDTO;
import com.api.reserva.dto.ReservaReferenciaDTO;
import com.api.reserva.entity.*;
import com.api.reserva.enums.Disponibilidade;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.*;
import com.api.reserva.repository.CatalogoRepository;
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.TurmaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.MetodosAuth;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservaService {
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CatalogoRepository catalogoRepository;
    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private NotificacaoService notificacaoService;

    public List<ReservaReferenciaDTO> buscar() {
        return reservaRepository.findAll()
                .stream()
                .map(ReservaReferenciaDTO::new)
                .toList();
    }

    public ReservaReferenciaDTO buscar(Long id) {
        return new ReservaReferenciaDTO(reservaRepository.findById(id).orElseThrow(
                SemResultadosException::new));
    }

    public List<ReservaReferenciaDTO> minhasReservas(Authentication authentication) {
        Long usuarioId = MetodosAuth.extrairId(authentication);

        // Buscar reservas onde o usuário é host
        Set<Reserva> comoHost = reservaRepository.findAllByHost_Id(usuarioId);

        // Buscar reservas onde o usuário é membro/participante
        Set<Reserva> comoMembro = reservaRepository.findAllByMembros_Id(usuarioId);

        // Unir resultados (evita duplicatas quando o usuário é host e membro de uma mesma reserva)
        Set<Reserva> todas = new HashSet<>();
        if (comoHost != null) {
            todas.addAll(comoHost);
        }
        if (comoMembro != null) {
            todas.addAll(comoMembro);
        }

        return todas.stream()
                .map(ReservaReferenciaDTO::new)
                .toList();
    }


    @Transactional
    public void salvar(ReservaDTO reservaDTO, Authentication authentication) {
        // Validações básicas
        validarDadosReserva(reservaDTO);

        // Buscar entidades necessárias
        Usuario host = usuarioRepository.findById(MetodosAuth.extrairId(authentication)).orElseThrow(
                () -> new SemResultadosException("Usuário"));

        // Validar se o usuário está em uma turma válida (apenas para estudantes)
        validarTurmaUsuario(host.getId(), authentication);

        Catalogo catalogo = catalogoRepository.findById(reservaDTO.getCatalogoId()).orElseThrow(
                () -> new SemResultadosException("Catálogo"));

        Ambiente ambiente = catalogo.getAmbiente();

        LocalDate dataDaReserva = reservaDTO.getData();
        LocalTime inicioReserva = reservaDTO.getHoraInicio();
        LocalTime fimReserva = reservaDTO.getHoraFim();

        // Validar se a reserva é futura ou para hoje com 15 minutos de antecedência
        LocalTime agora = LocalTime.now();
        LocalTime minimoDeTempo = agora.plusMinutes(15);

        // Se é para hoje
        if (dataDaReserva.equals(LocalDate.now())) {
            // Valida se o horário fim já não passou
            if (fimReserva.isBefore(agora) || fimReserva.equals(agora)) {
                throw new HorarioInvalidoException("A reserva deve ser para um horário futuro.");
            }
            // Valida se tem 15 minutos de antecedência antes do INÍCIO
            if (inicioReserva.isBefore(minimoDeTempo)) {
                throw new HorarioInvalidoException("A reserva deve ser feita com no mínimo 15 minutos de antecedência.");
            }
        }

        // Validar: usuário só pode ter uma reserva por dia por ambiente com status PENDENTE, APROVADA ou CONFIRMADA
        Set<Reserva> reservasDoHostNoAmbienteNoDia = reservaRepository.findAllByHost_idAndCatalogo_Ambiente_Id(host.getId(), ambiente.getId())
                .stream()
                .filter(r -> r.getData().equals(dataDaReserva) &&
                        (r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .collect(Collectors.toSet());



        if (!reservasDoHostNoAmbienteNoDia.isEmpty()) {
            System.out.println("Reservas do host no mesmo ambiente e dia:");
            reservasDoHostNoAmbienteNoDia.forEach(r -> System.out.println("Reserva id=" + r.getId() + " data=" + r.getData() + " inicio=" + r.getHoraInicio() + " fim=" + r.getHoraFim()));
            throw new SemPermissaoException("Você só pode fazer uma reserva por dia neste ambiente.");
        }

        // Validar: verificar se não há conflito de horário com reservas APROVADAS ou CONFIRMADAS de OUTROS usuários
        validarConflitosComOutrosUsuarios(ambiente, dataDaReserva, inicioReserva, fimReserva, host.getId());

        // Validar: verificar se o usuário não está em dois ambientes diferentes simultaneamente
        validarUsuarioNaoEstemOutroAmbiente(ambiente.getId(), dataDaReserva, inicioReserva, fimReserva, host.getId());

        // Validar: verificar se o usuário não está em duas reservas com horários sobrepostos no mesmo dia
        validarSobreposicaoHorariosDoUsuario(dataDaReserva, inicioReserva, fimReserva, host.getId());

        validarCatalogo(catalogo, reservaDTO);

        validarSobreposicaoHorarios(catalogo, reservaDTO);


        // Criar reserva
        Reserva reserva = new Reserva(
                host,
                catalogo,
                reservaDTO.getData(),
                reservaDTO.getHoraInicio(),
                reservaDTO.getHoraFim(),
                reservaDTO.getFinalidade()
        );

        if (reservaDTO.getMembrosIds() != null && !reservaDTO.getMembrosIds().isEmpty()) {
            reservaDTO.getMembrosIds().forEach(membroId -> {
                Usuario membro = usuarioRepository.findById(membroId).orElseThrow(
                        () -> new SemResultadosException("Membro não encontrado: " + membroId));
                reserva.getMembros().add(membro);
            });
        }

        // Verificar se o usuário é ADMIN, COORDENADOR ou PROFESSOR para aprovação automática
        Set<String> roles = MetodosAuth.extrairRole(authentication);
        boolean isAdminOrCoordenadorOrProfessor = roles != null &&
                (roles.contains("SCOPE_ADMIN") ||
                 roles.contains("SCOPE_COORDENADOR") ||
                 roles.contains("SCOPE_PROFESSOR"));

        // Apenas ADMIN, COORDENADOR e PROFESSOR têm aprovação automática
        // Estudantes SEMPRE ficam em PENDENTE, independentemente da configuração do ambiente
        if (isAdminOrCoordenadorOrProfessor) {
            reserva.setStatusReserva(StatusReserva.APROVADA);
        } else {
            // Estudantes ficam em PENDENTE
            reserva.setStatusReserva(StatusReserva.PENDENTE);
        }
        reservaRepository.save(reserva);

        // Notificar usuário sobre criação de reserva
        notificacaoService.novaNotificacao(
                host,
                "Reserva Criada ✓",
                "Sua reserva no ambiente '" + catalogo.getAmbiente().getNome() +
                "' para " + reservaDTO.getData() + " foi criada com sucesso."
        );
    }

    @Transactional
    public void atualizar(Long id, ReservaDTO reservaDTO, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("Reserva"));


//        if (!usuarioLogadoId.equals(reserva.getHost().getId()) &&
//            (role == null || !role.contains("SCOPE_ADMIN") && !role.contains("SCOPE_COORDENADOR"))) {
//            throw new SemPermissaoException();
//        }

        // Validações básicas
        validarDadosReserva(reservaDTO);

        // Validar se o usuário está em uma turma válida (apenas para estudantes)
        validarTurmaUsuario(reserva.getHost().getId(), authentication);

        // Buscar catálogo se mudou
        Catalogo catalogo;
        if (!reservaDTO.getCatalogoId().equals(reserva.getCatalogo().getId())) {
            catalogo = catalogoRepository.findById(reservaDTO.getCatalogoId()).orElseThrow(
                    () -> new SemResultadosException("Catálogo"));
            validarCatalogo(catalogo, reservaDTO);
        } else {
            catalogo = reserva.getCatalogo();
        }

        // Validar sobreposição excluindo a própria reserva
        validarSobreposicaoHorariosExcluindoReserva(catalogo, reservaDTO, id);

        // Validar conflitos com outros usuários
        validarConflitosComOutrosUsuariosExcluindoReserva(catalogo.getAmbiente(), reservaDTO.getData(),
                reservaDTO.getHoraInicio(), reservaDTO.getHoraFim(), reserva.getHost().getId(), id);

        // Validar se o usuário não está em outro ambiente no mesmo horário (excluindo a própria reserva)
        validarUsuarioNaoEstemOutroAmbienteExcluindoReserva(catalogo.getAmbiente().getId(), reservaDTO.getData(),
                reservaDTO.getHoraInicio(), reservaDTO.getHoraFim(), reserva.getHost().getId(), id);

        // Validar se o usuário não está em duas reservas sobrepostas (excluindo a própria)
        validarSobreposicaoHorariosDoUsuarioExcluindoReserva(reservaDTO.getData(), reservaDTO.getHoraInicio(),
                reservaDTO.getHoraFim(), reserva.getHost().getId(), id);

        // Atualizar dados
        reserva.setCatalogo(catalogo);
        reserva.setData(reservaDTO.getData());
        reserva.setHoraInicio(reservaDTO.getHoraInicio());
        reserva.setHoraFim(reservaDTO.getHoraFim());
        reserva.setFinalidade(reservaDTO.getFinalidade());

        reservaRepository.save(reserva);

        // Notificar usuário sobre atualização
        notificacaoService.novaNotificacao(
                reserva.getHost(),
                "Reserva Atualizada ✎",
                "Sua reserva no ambiente '" + catalogo.getAmbiente().getNome() +
                "' foi atualizada com sucesso."
        );
    }

    @Transactional
    public void deletar(Long id, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        // Verificar permissão
        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);
        Set<String> role = MetodosAuth.extrairRole(authentication);

        if (!usuarioLogadoId.equals(reserva.getHost().getId()) &&
                (role == null || !role.contains("SCOPE_ADMIN") && !role.contains("SCOPE_COORDENADOR"))) {
            throw new SemPermissaoException();
        }

        // Notificar usuário sobre deleção
        notificacaoService.novaNotificacao(
                reserva.getHost(),
                "Reserva Deletada ✗",
                "Sua reserva no ambiente '" + reserva.getCatalogo().getAmbiente().getNome() +
                "' para " + reserva.getData() + " foi deletada."
        );

        reservaRepository.delete(reserva);
    }

    @Transactional
    public void aprovar(Long id, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);
        Set<String> role = MetodosAuth.extrairRole(authentication);

        // Validar se é ADMIN
        boolean isAdmin = role != null && role.contains("SCOPE_ADMIN");

        // Validar se é coordenador do ambiente específico
        boolean isCoordenadorDoAmbiente = false;
        if (role != null && role.contains("SCOPE_COORDENADOR")) {
            Ambiente ambiente = reserva.getCatalogo().getAmbiente();
            isCoordenadorDoAmbiente = ambiente.getResponsavel() != null &&
                    ambiente.getResponsavel().getId().equals(usuarioLogadoId);
        }

        if (!isAdmin && !isCoordenadorDoAmbiente) {
            throw new SemPermissaoException("Você não tem permissão para aprovar esta reserva. Apenas o coordenador do ambiente pode aprovar.");
        }

        if(reserva.getStatusReserva() != StatusReserva.PENDENTE) {
            throw new HorarioInvalidoException("Apenas reservas pendentes podem ser aprovadas. Status atual: " + reserva.getStatusReserva());
        }

        reserva.setStatusReserva(StatusReserva.APROVADA);
        reservaRepository.save(reserva);

        // Notificar usuário sobre aprovação
        notificacaoService.novaNotificacao(
                reserva.getHost(),
                "Reserva Aprovada ✓",
                "Sua reserva no ambiente '" + reserva.getCatalogo().getAmbiente().getNome() +
                "' para " + reserva.getData() + " foi APROVADA."
        );
    }

    @Transactional
    public void rejeitar(Long id, String motivo, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);
        Set<String> role = MetodosAuth.extrairRole(authentication);

        // Validar se é ADMIN
        boolean isAdmin = role != null && role.contains("SCOPE_ADMIN");

        // Validar se é coordenador do ambiente específico
        boolean isCoordenadorDoAmbiente = false;
        if (role != null && role.contains("SCOPE_COORDENADOR")) {
            Ambiente ambiente = reserva.getCatalogo().getAmbiente();
            isCoordenadorDoAmbiente = ambiente.getResponsavel() != null &&
                    ambiente.getResponsavel().getId().equals(usuarioLogadoId);
        }

        if (!isAdmin && !isCoordenadorDoAmbiente) {
            throw new SemPermissaoException("Você não tem permissão para rejeitar esta reserva. Apenas o coordenador do ambiente pode rejeitar.");
        }

        if(reserva.getStatusReserva() != StatusReserva.PENDENTE) {
            throw new HorarioInvalidoException("Apenas reservas pendentes podem ser negadas. Status atual: " + reserva.getStatusReserva());
        }

        reserva.setStatusReserva(StatusReserva.NEGADA);
        reservaRepository.save(reserva);

        // Criar notificação imediata para o usuário
        notificacaoService.novaNotificacao(
                reserva.getHost(),
                "Reserva Rejeitada ✗",
                "Sua reserva no ambiente '" + reserva.getCatalogo().getAmbiente().getNome() +
                "' para " + reserva.getData() + " foi REJEITADA.\n" +
                "Motivo: " + motivo
        );
    }

    private void validarDadosReserva(ReservaDTO reservaDTO) {
        // Validar se a data é futura
        if (reservaDTO.getData().isBefore(LocalDate.now())) {
            throw new DataInvalidaException("A data da reserva deve ser futura ou hoje");
        }

        // Validar horários
        ValidacaoDatasEHorarios.validarHorarios(reservaDTO.getHoraInicio(), reservaDTO.getHoraFim());
        ValidacaoDatasEHorarios.atendeDuracaoMinima(reservaDTO.getHoraInicio(), reservaDTO.getHoraFim());
    }

    private void validarTurmaUsuario(Long usuarioId, Authentication authentication) {
        // Validação só se aplica a estudantes
        Set<String> roles = MetodosAuth.extrairRole(authentication);

        // Se o usuário não é estudante (é professor, coordenador ou admin), pular validação
        if (roles == null || !roles.contains("SCOPE_ESTUDANTE")) {
            return;
        }

        // Buscar todas as turmas do usuário
        List<Turma> turmasDoUsuario = turmaRepository.findAllByEstudantes_Id(usuarioId);

        // Verificar se o usuário está em pelo menos uma turma com data válida
        LocalDate dataAtual = LocalDate.now();
        boolean temTurmaValida = turmasDoUsuario.stream()
                .anyMatch(turma -> {
                    LocalDate dataInicio = turma.getDataInicio();
                    LocalDate dataTermino = turma.getDataTermino();
                    // Validar: a turma já iniciou E ainda não terminou
                    return !dataInicio.isAfter(dataAtual) && !dataTermino.isBefore(dataAtual);
                });

        if (!temTurmaValida) {
            throw new TurmaInvalidaException();
        }
    }

    private void validarCatalogo(Catalogo catalogo, ReservaDTO reservaDTO) {
        // Verificar se o catálogo está disponível
        if (catalogo.getDisponibilidade() != Disponibilidade.DISPONIVEL) {
            throw new HorarioInvalidoException("O catálogo não está disponível para reservas");
        }

        // Verificar se o dia da semana coincide
        if (reservaDTO.getData().getDayOfWeek() != catalogo.getDiaSemana().getDayOfWeek()) {
            throw new DataInvalidaException("A data não corresponde ao dia da semana do catálogo");
        }

        // Verificar se os horários estão dentro do limite do catálogo
        if (reservaDTO.getHoraInicio().isBefore(catalogo.getHoraInicio()) ||
                reservaDTO.getHoraFim().isAfter(catalogo.getHoraFim())) {
            throw new HorarioInvalidoException("Os horários da reserva devem estar dentro do horário do catálogo");
        }
    }

    private void validarSobreposicaoHorarios(Catalogo catalogo, ReservaDTO reservaDTO) {
        //
        List<Reserva> reservasExistentesDoCatalogo = reservaRepository.findAllByCatalogo_Id(catalogo.getId())
                .stream()
                .filter(r -> r.getData().equals(reservaDTO.getData()) &&
                        r.getStatusReserva() != StatusReserva.CANCELADA &&
                        r.getStatusReserva() != StatusReserva.NEGADA)
                .toList();

        for (Reserva reservaExistente : reservasExistentesDoCatalogo) {
            if (horariosSesobrepem(
                    reservaDTO.getHoraInicio(), reservaDTO.getHoraFim(),
                    reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Já existe uma reserva neste horário");
            }
        }
    }

    private boolean horariosSesobrepem(LocalTime inicio1, LocalTime fim1, LocalTime inicio2, LocalTime fim2) {
        return inicio1.isBefore(fim2) && fim1.isAfter(inicio2);
    }

    /**
     * Valida se o usuário não está em outro AMBIENTE no mesmo horário.
     * Isso garante que o usuário não possa estar em 2 ambientes diferentes simultaneamente.
     *
     * @param ambienteId ID do ambiente onde o usuário tenta fazer a reserva
     * @param data data da reserva
     * @param inicioReserva hora de início
     * @param fimReserva hora de fim
     * @param usuarioId ID do usuário
     */
    private void validarUsuarioNaoEstemOutroAmbiente(Long ambienteId, LocalDate data, LocalTime inicioReserva, LocalTime fimReserva, Long usuarioId) {
        // Buscar todas as reservas do usuário como HOST em outros ambientes
        List<Reserva> reservasComoHostEmOutrosAmbientes = reservaRepository.findAllByHost_Id(usuarioId)
                .stream()
                .filter(r -> r.getData().equals(data) &&
                        !r.getCatalogo().getAmbiente().getId().equals(ambienteId) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .toList();

        // Buscar todas as reservas do usuário como MEMBRO em outros ambientes
        List<Reserva> reservasComoMembroEmOutrosAmbientes = reservaRepository.findAllByMembros_Id(usuarioId)
                .stream()
                .filter(r -> r.getData().equals(data) &&
                        !r.getCatalogo().getAmbiente().getId().equals(ambienteId) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .toList();

        // Combinar as duas listas
        List<Reserva> todasAsReservasEmOutrosAmbientes = new java.util.ArrayList<>();
        todasAsReservasEmOutrosAmbientes.addAll(reservasComoHostEmOutrosAmbientes);
        todasAsReservasEmOutrosAmbientes.addAll(reservasComoMembroEmOutrosAmbientes);

        // Verificar se há sobreposição de horário
        for (Reserva reservaExistente : todasAsReservasEmOutrosAmbientes) {
            if (horariosSesobrepem(inicioReserva, fimReserva, reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Você já está em outro ambiente neste horário. Não é permitido estar em dois ambientes diferentes simultaneamente.");
            }
        }
    }

    private void validarSobreposicaoHorariosExcluindoReserva(Catalogo catalogo, ReservaDTO reservaDTO, Long reservaId) {
        List<Reserva> reservasExistentes = reservaRepository.findAllByCatalogo_Id(catalogo.getId())
                .stream()
                .filter(r -> !r.getId().equals(reservaId) &&
                        r.getData().equals(reservaDTO.getData()) &&
                        r.getStatusReserva() != StatusReserva.CANCELADA &&
                        r.getStatusReserva() != StatusReserva.NEGADA)
                .toList();

        for (Reserva reservaExistente : reservasExistentes) {
            if (horariosSesobrepem(
                    reservaDTO.getHoraInicio(), reservaDTO.getHoraFim(),
                    reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Já existe uma reserva neste horário");
            }
        }
    }

    private void validarConflitosComOutrosUsuariosExcluindoReserva(Ambiente ambiente, LocalDate data, LocalTime inicioReserva,
                                                                   LocalTime fimReserva, Long usuarioHostId, Long reservaId) {
        // Buscar todas as reservas APROVADAS ou CONFIRMADAS no ambiente para a data escolhida (excluindo a atual)
        List<Reserva> reservasAprovadosOuConfirmados = reservaRepository.findAllByCatalogo_Ambiente_Id(ambiente.getId())
                .stream()
                .filter(r -> !r.getId().equals(reservaId) &&
                        r.getData().equals(data) &&
                        !r.getHost().getId().equals(usuarioHostId) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA))
                .toList();

        // Verificar se há conflito de horário
        for (Reserva reservaExistente : reservasAprovadosOuConfirmados) {
            if (horariosSesobrepem(inicioReserva, fimReserva, reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Já existe uma reserva APROVADA ou CONFIRMADA de outro usuário neste horário neste ambiente.");
            }
        }
    }

    private void validarUsuarioNaoEstemOutroAmbienteExcluindoReserva(Long ambienteId, LocalDate data, LocalTime inicioReserva,
                                                                     LocalTime fimReserva, Long usuarioId, Long reservaId) {
        // Buscar todas as reservas do usuário como HOST em outros ambientes (excluindo a atual)
        List<Reserva> reservasComoHostEmOutrosAmbientes = reservaRepository.findAllByHost_Id(usuarioId)
                .stream()
                .filter(r -> !r.getId().equals(reservaId) &&
                        r.getData().equals(data) &&
                        !r.getCatalogo().getAmbiente().getId().equals(ambienteId) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .toList();

        // Buscar todas as reservas do usuário como MEMBRO em outros ambientes (excluindo a atual)
        List<Reserva> reservasComoMembroEmOutrosAmbientes = reservaRepository.findAllByMembros_Id(usuarioId)
                .stream()
                .filter(r -> !r.getId().equals(reservaId) &&
                        r.getData().equals(data) &&
                        !r.getCatalogo().getAmbiente().getId().equals(ambienteId) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .toList();

        // Combinar as duas listas
        List<Reserva> todasAsReservasEmOutrosAmbientes = new java.util.ArrayList<>();
        todasAsReservasEmOutrosAmbientes.addAll(reservasComoHostEmOutrosAmbientes);
        todasAsReservasEmOutrosAmbientes.addAll(reservasComoMembroEmOutrosAmbientes);

        // Verificar se há sobreposição de horário
        for (Reserva reservaExistente : todasAsReservasEmOutrosAmbientes) {
            if (horariosSesobrepem(inicioReserva, fimReserva, reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Você já está em outro ambiente neste horário. Não é permitido estar em dois ambientes diferentes simultaneamente.");
            }
        }
    }

    private void validarSobreposicaoHorariosDoUsuarioExcluindoReserva(LocalDate data, LocalTime inicioReserva,
                                                                      LocalTime fimReserva, Long usuarioId, Long reservaId) {
        // Buscar todas as reservas do usuário como HOST (excluindo a atual)
        List<Reserva> reservasComoHost = reservaRepository.findAllByHost_Id(usuarioId)
                .stream()
                .filter(r -> !r.getId().equals(reservaId) &&
                        r.getData().equals(data) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .toList();

        // Buscar todas as reservas do usuário como MEMBRO (excluindo a atual)
        List<Reserva> reservasComoMembro = reservaRepository.findAllByMembros_Id(usuarioId)
                .stream()
                .filter(r -> !r.getId().equals(reservaId) &&
                        r.getData().equals(data) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .toList();

        // Combinar as duas listas
        List<Reserva> todasAsReservasDoUsuario = new java.util.ArrayList<>();
        todasAsReservasDoUsuario.addAll(reservasComoHost);
        todasAsReservasDoUsuario.addAll(reservasComoMembro);

        // Verificar se há sobreposição de horário
        for (Reserva reservaExistente : todasAsReservasDoUsuario) {
            if (horariosSesobrepem(inicioReserva, fimReserva, reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Você já está em uma reserva neste horário. Não é permitido estar em duas reservas com horários sobrepostos no mesmo dia.");
            }
        }
    }

    private void validarConflitosComOutrosUsuarios(Ambiente ambiente, LocalDate data, LocalTime inicioReserva, LocalTime fimReserva, Long usuarioHostId) {
        // Buscar todas as reservas APROVADAS ou CONFIRMADAS no ambiente para a data escolhida
        List<Reserva> reservasAprovadosOuConfirmados = reservaRepository.findAllByCatalogo_Ambiente_Id(ambiente.getId())
                .stream()
                .filter(r -> r.getData().equals(data) &&
                        !r.getHost().getId().equals(usuarioHostId) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA))
                .toList();

        // Verificar se há conflito de horário
        for (Reserva reservaExistente : reservasAprovadosOuConfirmados) {
            if (horariosSesobrepem(inicioReserva, fimReserva, reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Já existe uma reserva APROVADA ou CONFIRMADA de outro usuário neste horário neste ambiente.");
            }
        }
    }

    private void validarSobreposicaoHorariosDoUsuario(LocalDate data, LocalTime inicioReserva, LocalTime fimReserva, Long usuarioId) {
        // Buscar todas as reservas do usuário como HOST
        List<Reserva> reservasComoHost = reservaRepository.findAllByHost_Id(usuarioId)
                .stream()
                .filter(r -> r.getData().equals(data) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .toList();

        // Buscar todas as reservas do usuário como MEMBRO
        List<Reserva> reservasComoMembro = reservaRepository.findAllByMembros_Id(usuarioId)
                .stream()
                .filter(r -> r.getData().equals(data) &&
                        (r.getStatusReserva() == StatusReserva.APROVADA ||
                         r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                         r.getStatusReserva() == StatusReserva.PENDENTE ||
                         r.getStatusReserva() == StatusReserva.ACONTECENDO))
                .toList();

        // Combinar as duas listas
        List<Reserva> todasAsReservasDoUsuario = new java.util.ArrayList<>();
        todasAsReservasDoUsuario.addAll(reservasComoHost);
        todasAsReservasDoUsuario.addAll(reservasComoMembro);

        // Verificar se há sobreposição de horário
        for (Reserva reservaExistente : todasAsReservasDoUsuario) {
            if (horariosSesobrepem(inicioReserva, fimReserva, reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Você já está em uma reserva neste horário. Não é permitido estar em duas reservas com horários sobrepostos no mesmo dia.");
            }
        }
    }

//    private void adicionarConvidados(Reserva reserva, Set<Long> convidadosIds) {
//        Set<Usuario> convidados = new HashSet<>();
//
//        for (Long convidadoId : convidadosIds) {
//            Usuario convidado = usuarioRepository.findById(convidadoId).orElseThrow(
//                    () -> new SemResultadosException("Convidado não encontrado: " + convidadoId));
//
//            Usuario reservaConvidado = new ReservaConvidados();
//            reservaConvidado.setReserva(reserva);
//            reservaConvidado.setConvidado(convidado);
//            convidados.add(reservaConvidado);
//        }
//
//        reserva.setParticipantes(convidados);
//        reservaConvidadosRepository.saveAll(convidados);
//    }
//
//    private void atualizarConvidados(Reserva reserva, Set<Long> novosConvidadosIds) {
//        // Remover convidados existentes
//        if (reserva.getParticipantes() != null) {
//            reservaConvidadosRepository.deleteAll(reserva.getParticipantes());
//            reserva.getParticipantes().clear();
//        }
//
//        // Adicionar novos convidados
//        if (novosConvidadosIds != null && !novosConvidadosIds.isEmpty()) {
//            adicionarConvidados(reserva, novosConvidadosIds);
//        }
//    }

    @Transactional
    public void ingressarViacodigo(String codigo, Authentication authentication) {
        // Buscar reserva pelo código
        Reserva reserva = reservaRepository.findByCodigo(codigo).orElseThrow(
                () -> new SemResultadosException("Reserva com código " + codigo + " não encontrada"));

        // Buscar usuário logado
        Usuario usuario = usuarioRepository.findById(MetodosAuth.extrairId(authentication)).orElseThrow(
                () -> new SemResultadosException("Usuário"));

        // Validação: Usuário não pode ser o host
        if (reserva.getHost().getId().equals(usuario.getId())) {
            throw new SemPermissaoException("O host da reserva não pode ingressar como participante.");
        }

        // Validação: Usuário não pode estar já na reserva
        if (reserva.getMembros().contains(usuario)) {
            throw new SemPermissaoException("Você já está nesta reserva.");
        }

        // Validação: Verificar se há conflito de horário com reservas APROVADAS ou CONFIRMADAS de OUTROS usuários
        validarConflitosComOutrosUsuarios(reserva.getCatalogo().getAmbiente(), reserva.getData(),
                reserva.getHoraInicio(), reserva.getHoraFim(), usuario.getId());

        // Validação: Verificar se o usuário não está em outro ambiente no mesmo horário
        validarUsuarioNaoEstemOutroAmbiente(reserva.getCatalogo().getAmbiente().getId(), reserva.getData(),
                reserva.getHoraInicio(), reserva.getHoraFim(), usuario.getId());

        // Validação: Verificar se o usuário não está em duas reservas com horários sobrepostos no mesmo dia
        validarSobreposicaoHorariosDoUsuario(reserva.getData(), reserva.getHoraInicio(),
                reserva.getHoraFim(), usuario.getId());

        // Validação: Verificar se a reserva está em status válido (PENDENTE, APROVADA, CONFIRMADA)
        if (reserva.getStatusReserva() == StatusReserva.CANCELADA ||
            reserva.getStatusReserva() == StatusReserva.NEGADA ||
            reserva.getStatusReserva() == StatusReserva.ACONTECENDO) {
            throw new HorarioInvalidoException("Esta reserva não está disponível para ingressar.");
        }

        // Adicionar usuário aos membros
        reserva.getMembros().add(usuario);
        reservaRepository.save(reserva);

        // Notificar usuário que ingressou
        notificacaoService.novaNotificacao(
                usuario,
                "Você Ingressou ✓",
                "Você ingressou na reserva do ambiente '" + reserva.getCatalogo().getAmbiente().getNome() +
                "' para " + reserva.getData() + "."
        );

        // Notificar host que um novo membro ingressou
        notificacaoService.novaNotificacao(
                reserva.getHost(),
                "Novo Membro na Reserva 👤",
                usuario.getNome() + " ingressou em sua reserva no ambiente '" +
                reserva.getCatalogo().getAmbiente().getNome() + "'."
        );
    }

    @Transactional
    public void sairDaReserva(Long reservaId, Authentication authentication) {
        // Buscar reserva
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        // Buscar usuário logado
        Usuario usuario = usuarioRepository.findById(MetodosAuth.extrairId(authentication)).orElseThrow(
                () -> new SemResultadosException("Usuário"));

        // Validação: Usuário não pode ser o host
        if (reserva.getHost().getId().equals(usuario.getId())) {
            throw new SemPermissaoException("O host da reserva não pode sair como participante.");
        }

        // Validação: Usuário deve estar na reserva
        if (!reserva.getMembros().contains(usuario)) {
            throw new SemPermissaoException("Você não está nesta reserva.");
        }

        // Validação: Não pode sair se a reserva já está acontecendo ou finalizou
        if (reserva.getStatusReserva() == StatusReserva.ACONTECENDO ||
            reserva.getStatusReserva() == StatusReserva.CANCELADA ||
            reserva.getStatusReserva() == StatusReserva.NEGADA) {
            throw new HorarioInvalidoException("Você não pode sair desta reserva neste estado.");
        }

        // Remover usuário dos membros
        reserva.getMembros().remove(usuario);
        reservaRepository.save(reserva);

        // Notificar usuário que saiu
        notificacaoService.novaNotificacao(
                usuario,
                "Você Saiu da Reserva ⬅️",
                "Você saiu da reserva do ambiente '" + reserva.getCatalogo().getAmbiente().getNome() +
                "' para " + reserva.getData() + "."
        );

        // Notificar host que um membro saiu
        notificacaoService.novaNotificacao(
                reserva.getHost(),
                "Membro Saiu da Reserva 👤",
                usuario.getNome() + " saiu de sua reserva no ambiente '" +
                reserva.getCatalogo().getAmbiente().getNome() + "'."
        );
    }

    @Transactional
    public void gerarNovoCodigoReserva(Long reservaId, Authentication authentication) {
        // Buscar reserva
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        // Buscar usuário logado
        Usuario usuario = usuarioRepository.findById(MetodosAuth.extrairId(authentication)).orElseThrow(
                () -> new SemResultadosException("Usuário"));

        // Validação: Apenas o host pode gerar novo código
        if (!reserva.getHost().getId().equals(usuario.getId())) {
            throw new SemPermissaoException("Apenas o host da reserva pode gerar um novo código.");
        }

        // Gerar novo código
        reserva.setCodigo(com.api.reserva.util.CodigoUtil.gerarCodigo(5));
        reservaRepository.save(reserva);

        // Notificar usuário que novo código foi gerado
        notificacaoService.novaNotificacao(
                usuario,
                "Novo Código Gerado 🔑",
                "Um novo código foi gerado para sua reserva no ambiente '" +
                reserva.getCatalogo().getAmbiente().getNome() + "'. Novo código: " + reserva.getCodigo()
        );
    }

    @Transactional
    public void removerParticipanteDaReserva(Long reservaId, Long participanteId, Authentication authentication) {
        // Buscar reserva
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        // Buscar usuário logado
        Usuario usuarioLogado = usuarioRepository.findById(MetodosAuth.extrairId(authentication)).orElseThrow(
                () -> new SemResultadosException("Usuário"));

        // Buscar participante a ser removido
        Usuario participante = usuarioRepository.findById(participanteId).orElseThrow(
                () -> new SemResultadosException("Participante não encontrado"));

        // Validação: Apenas o host pode remover participantes
        if (!reserva.getHost().getId().equals(usuarioLogado.getId())) {
            throw new SemPermissaoException("Apenas o host da reserva pode remover participantes.");
        }

        // Validação: Participante deve estar na reserva
        if (!reserva.getMembros().contains(participante)) {
            throw new SemPermissaoException("Este usuário não está nesta reserva.");
        }

        // Validação: Não pode remover se a reserva já está acontecendo
        if (reserva.getStatusReserva() == StatusReserva.ACONTECENDO) {
            throw new HorarioInvalidoException("Não é permitido remover participantes de uma reserva que já está acontecendo.");
        }

        // Remover participante
        reserva.getMembros().remove(participante);
        reservaRepository.save(reserva);

        // Notificar participante que foi removido
        notificacaoService.novaNotificacao(
                participante,
                "Removido da Reserva 🚫",
                "Você foi removido da reserva no ambiente '" +
                reserva.getCatalogo().getAmbiente().getNome() + "' pelo host."
        );

        // Notificar host que removeu participante
        notificacaoService.novaNotificacao(
                usuarioLogado,
                "Participante Removido 👤",
                participante.getNome() + " foi removido da sua reserva no ambiente '" +
                reserva.getCatalogo().getAmbiente().getNome() + "'."
        );
    }

    @Transactional
    public void cancelarReserva(Long reservaId, String motivo, Authentication authentication) {
        // Buscar reserva
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));


        // Buscar ambiente da reserva
        Ambiente ambiente = reserva.getCatalogo().getAmbiente();

        // Extrair roles do usuário
        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);
        Set<String> role = MetodosAuth.extrairRole(authentication);

        // Validar permissão: Apenas ADMIN, HOST ou COORDENADOR do ambiente
        boolean isAdmin = role != null && role.contains("SCOPE_ADMIN");
        boolean isHost = reserva.getHost().getId().equals(usuarioLogadoId);
        boolean isCoordenadorDoAmbiente = false;

        if (role != null && role.contains("SCOPE_COORDENADOR")) {
            isCoordenadorDoAmbiente = ambiente.getResponsavel() != null &&
                    ambiente.getResponsavel().getId().equals(usuarioLogadoId);
        }

        if (!isAdmin && !isHost && !isCoordenadorDoAmbiente) {
            throw new SemPermissaoException("Você não tem permissão para cancelar esta reserva. Apenas ADMIN, host ou coordenador do ambiente podem cancelar.");
        }

        // Validar status: Não pode cancelar se já foi CANCELADA ou NEGADA
        if (reserva.getStatusReserva() == StatusReserva.CANCELADA || reserva.getStatusReserva() == StatusReserva.NEGADA) {
            throw new HorarioInvalidoException("Esta reserva já foi " + reserva.getStatusReserva().toString().toLowerCase() + ".");
        }

        // Salvar motivo
        String motivoCancelamento = motivo != null ? motivo : "Cancelamento sem motivo especificado";

        // Cancelar reserva
        reserva.setStatusReserva(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);

        // Notificar host
        notificacaoService.novaNotificacao(
                reserva.getHost(),
                "Reserva Cancelada ❌",
                "Sua reserva no ambiente '" + reserva.getCatalogo().getAmbiente().getNome() +
                "' para " + reserva.getData() + " foi CANCELADA.\nMotivo: " + motivoCancelamento
        );

        // Notificar todos os membros
        for (Usuario membro : reserva.getMembros()) {
            notificacaoService.novaNotificacao(
                    membro,
                    "Reserva Cancelada ❌",
                    "A reserva no ambiente '" + reserva.getCatalogo().getAmbiente().getNome() +
                    "' para " + reserva.getData() + " foi CANCELADA.\nMotivo: " + motivoCancelamento
            );
        }
    }

    public Set<ReservaReferenciaDTO> buscarPorStatus(StatusReserva statusReserva) {
        Set<ReservaReferenciaDTO> reservas = reservaRepository.findAllByStatusReserva(statusReserva)
                .stream()
                .filter(r -> r.getStatusReserva() == statusReserva)
                .map(ReservaReferenciaDTO::new)
                .collect(Collectors.toSet());

        if(reservas.isEmpty()) {
            throw new SemResultadosException("Reservas com status: " + statusReserva);
        } else {
            return reservas;
        }
    }
}
