package com.api.reserva.service;

import com.api.reserva.dto.AmbienteDTO;
import com.api.reserva.dto.AmbienteReferenciaDTO;
import com.api.reserva.entity.*;
import com.api.reserva.enums.Aprovacao;
import com.api.reserva.enums.Disponibilidade;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.DadoDuplicadoException;
import com.api.reserva.exception.EntidadeJaExistente;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.AmbienteRepository;
import com.api.reserva.repository.CatalogoRepository;
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.MetodosAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo gerenciamento de operações relacionadas a entidade Ambiente.
 */
@Service
public class AmbienteService {
    @Autowired
    AmbienteRepository ambienteRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private CatalogoRepository catalogoRepository;
    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * Busca um ambiente específico pelo seu ID
     *
     * @param id identificador único do ambienteb
     * @return o DTO do ambiente encontrado
     * @throws SemResultadosException se nenhum ambiente com id fornecido for encontraodo
     */
    public AmbienteReferenciaDTO buscar(Long id) {
        return new AmbienteReferenciaDTO(ambienteRepository.findById(id).orElseThrow(SemResultadosException::new));
    }

    /**
     * Busca todos os ambientes
     *
     * @return uma lista de DTOs de todos os ambientes
     */
    public List<AmbienteReferenciaDTO>buscar() {
        List<Ambiente> ambientes = ambienteRepository.findAll();
        return ambientes.stream()
                .map(AmbienteReferenciaDTO::new)
                .toList();
    }

    /**
     * Salva um novo ambiente
     *
     * @param ambienteDTO os dados do ambiente
     * @throws DadoDuplicadoException caso já haja um dado unico existente
     */
    @Transactional
    public void salvar(AmbienteDTO ambienteDTO) {

        if (ambienteRepository.existsByNome(ambienteDTO.getNome())) {
            throw new EntidadeJaExistente("Ambiente");
        }

        Ambiente ambiente = new Ambiente(ambienteDTO);
        ambiente.setNome(ambienteDTO.getNome());
        ambiente.setDescricao(ambienteDTO.getDescricao());
        ambiente.setDisponibilidade(ambienteDTO.getDisponibilidade());
        ambiente.setAprovacao(ambienteDTO.getAprovacao());
        ambiente.setEmUso(false);
        ambiente.setRecurso(ambienteDTO.isRecurso());
        ambiente.setSoInternos(ambienteDTO.isSoInternos());

        if (ambienteDTO.getResponsavelId() != null) {
            Usuario responsavel = usuarioRepository.findById(ambienteDTO.getResponsavelId())
                    .orElseThrow(() -> new SemResultadosException("Responsável não encontrado"));

            if (!responsavel.getRoles().stream()
                    .anyMatch(role -> role.getRoleNome() == Role.Values.COORDENADOR)) {
                throw new SemPermissaoException("Responsável deve ter role COORDENADOR");
            }

            ambiente.setResponsavel(responsavel);
        }

        ambienteRepository.save(ambiente);
    }

    /*
     * Atualiza um ambiente
     *
     * @param ambienteDTO os novos dados ao ambiente
     * @param id          identificador do ambiente a ser atualizado
     * @return um DTO contendo os novos dados
     * @throws SemResultadosException caso não encontre o ambiente pelo id
     * @throws DadoDuplicadoException caso já haja um dado unico existente
     */
    @Transactional
    public void atualizar(Long id, AmbienteDTO ambienteDTO, Authentication authentication) {
        Ambiente ambiente = ambienteRepository.findById(id).orElseThrow(() -> new SemResultadosException("atualização"));

        if (ambienteRepository.existsByNomeAndIdNot(ambienteDTO.getNome(), id)) {
            throw new EntidadeJaExistente("Ambiente");
        }

        Long usuarioId = MetodosAuth.extrairId(authentication);
        Usuario usuarioAutenticado = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new SemResultadosException("Usuário"));

        boolean ehAdmin = usuarioAutenticado.getRoles().stream()
                .anyMatch(role -> role.getRoleNome() == Role.Values.ADMIN);

        boolean ehResponsavel = ambiente.getResponsavel() != null &&
                ambiente.getResponsavel().getId().equals(usuarioId);

        if (!ehAdmin && !ehResponsavel) {
            throw new SemPermissaoException("Apenas ADMIN ou o responsável pode atualizar este ambiente");
        }

        ambiente.setNome(ambienteDTO.getNome());
        ambiente.setDescricao(ambienteDTO.getDescricao());
        ambiente.setRecurso(ambienteDTO.isRecurso());
        ambiente.setSoInternos(ambienteDTO.isSoInternos());

        // Se disponibilidade mudou para indisponível -> usar método específico
        if (ambiente.getDisponibilidade() != ambienteDTO.getDisponibilidade() &&
                ambienteDTO.getDisponibilidade() == Disponibilidade.INDISPONIVEL) {
            indisponibilizarAmbiente(id);
        }
        // Se disponibilidade mudou para disponível -> usar método específico
        else if (ambiente.getDisponibilidade() != ambienteDTO.getDisponibilidade() &&
                ambienteDTO.getDisponibilidade() == Disponibilidade.DISPONIVEL) {
            disponibilizarAmbiente(id);
        }

        // Tratar mudança no tipo de aprovação do ambiente
        if (ambiente.getAprovacao() != ambienteDTO.getAprovacao()) {
            Aprovacao aprovacaoAntiga = ambiente.getAprovacao();
            Aprovacao aprovacaoNova = ambienteDTO.getAprovacao();

            // Mudou de MANUAL para AUTOMATICA -> Aprovar todas as reservas PENDENTES
            if (aprovacaoAntiga == Aprovacao.MANUAL && aprovacaoNova == Aprovacao.AUTOMATICA) {
                Set<Reserva> reservasPendentes = reservaRepository.findAllByCatalogo_Ambiente_Id(id)
                        .stream()
                        .filter(r -> r.getStatusReserva() == StatusReserva.PENDENTE)
                        .collect(Collectors.toSet());

                if (!reservasPendentes.isEmpty()) {
                    reservasPendentes.forEach(reserva -> {
                        reserva.setStatusReserva(StatusReserva.APROVADA);
                        reservaRepository.save(reserva);

                        // Notificar host da aprovação automática
                        notificacaoService.novaNotificacao(
                                reserva.getHost(),
                                "Reserva Aprovada Automaticamente ✓",
                                "Sua reserva no ambiente '" + ambiente.getNome() +
                                "' (código: " + reserva.getCodigo() + ") foi aprovada automaticamente. " +
                                "O ambiente agora possui aprovação automática."
                        );
                    });

                    // Notificar administradores sobre a mudança
                    notificarAdminsECoordenador(
                            ambiente,
                            "Aprovação Automática Ativada 🔄",
                            "O ambiente '" + ambiente.getNome() + "' mudou para aprovação AUTOMÁTICA. " +
                            reservasPendentes.size() + " reserva(s) pendente(s) foram aprovadas automaticamente."
                    );
                }
            }
            // Mudou de AUTOMATICA para MANUAL -> Apenas notificar, reservas aprovadas permanecem aprovadas
            else if (aprovacaoAntiga == Aprovacao.AUTOMATICA && aprovacaoNova == Aprovacao.MANUAL) {
                // Notificar administradores sobre a mudança
                notificarAdminsECoordenador(
                        ambiente,
                        "Aprovação Manual Ativada 🔄",
                        "O ambiente '" + ambiente.getNome() + "' mudou para aprovação MANUAL. " +
                        "Novas reservas de estudantes precisarão de aprovação manual. " +
                        "Reservas já aprovadas permanecem inalteradas."
                );

                // Notificar usuários com reservas futuras no ambiente
                Set<Reserva> reservasFuturas = reservaRepository.findAllByCatalogo_Ambiente_Id(id)
                        .stream()
                        .filter(r -> r.getData().isAfter(LocalDate.now()) || r.getData().equals(LocalDate.now()))
                        .filter(r -> r.getStatusReserva() == StatusReserva.APROVADA ||
                                     r.getStatusReserva() == StatusReserva.CONFIRMADA)
                        .collect(Collectors.toSet());

                Set<Usuario> usuariosNotificados = new HashSet<>();
                reservasFuturas.forEach(reserva -> {
                    if (!usuariosNotificados.contains(reserva.getHost())) {
                        notificacaoService.novaNotificacao(
                                reserva.getHost(),
                                "Mudança no Tipo de Aprovação ℹ️",
                                "O ambiente '" + ambiente.getNome() + "' mudou para aprovação MANUAL. " +
                                "Suas reservas atuais permanecem válidas, mas novas reservas de estudantes precisarão de aprovação."
                        );
                        usuariosNotificados.add(reserva.getHost());
                    }
                });
            }
        }

        // ✅ TRATAR MUDANÇA NO ATRIBUTO soInternos
        if (ambiente.isSoInternos() != ambienteDTO.isSoInternos()) {
            boolean soInternosAntigo = ambiente.isSoInternos();
            boolean soInternosNovo = ambienteDTO.isSoInternos();

            // Mudou de FALSE para TRUE -> Cancelar reservas de estudantes puros
            if (!soInternosAntigo && soInternosNovo) {
                // Buscar todas as reservas futuras/ativas
                Set<Reserva> reservasAtivas = reservaRepository.findAllByCatalogo_Ambiente_Id(id)
                        .stream()
                        .filter(r -> r.getData().isAfter(LocalDate.now()) || r.getData().equals(LocalDate.now()))
                        .filter(r -> r.getStatusReserva() == StatusReserva.PENDENTE ||
                                     r.getStatusReserva() == StatusReserva.APROVADA ||
                                     r.getStatusReserva() == StatusReserva.CONFIRMADA ||
                                     r.getStatusReserva() == StatusReserva.ACONTECENDO)
                        .collect(Collectors.toSet());

                Set<Usuario> usuariosNotificados = new HashSet<>();

                reservasAtivas.forEach(reserva -> {
                    Usuario host = reserva.getHost();
                    // Verificar se é estudante puro
                    boolean ehApenaEstudante = host.getRoles().size() == 1 &&
                            host.getRoles().stream()
                                    .anyMatch(role -> role.getRoleNome() == Role.Values.ESTUDANTE);

                    if (ehApenaEstudante) {
                        // Cancelar a reserva
                        reserva.setStatusReserva(StatusReserva.CANCELADA);
                        reservaRepository.save(reserva);

                        // Notificar host
                        notificacaoService.novaNotificacao(
                                host,
                                "Reserva Cancelada Automaticamente ❌",
                                "Sua reserva no ambiente '" + ambiente.getNome() +
                                "' para " + reserva.getData() + " foi CANCELADA AUTOMATICAMENTE. " +
                                "Este ambiente agora é restrito apenas para uso interno."
                        );

                        usuariosNotificados.add(host);

                        // Notificar todos os membros também
                        for (Usuario membro : reserva.getMembros()) {
                            notificacaoService.novaNotificacao(
                                    membro,
                                    "Reserva Cancelada Automaticamente ❌",
                                    "A reserva no ambiente '" + ambiente.getNome() +
                                    "' para " + reserva.getData() + " foi CANCELADA AUTOMATICAMENTE. " +
                                    "Este ambiente agora é restrito apenas para uso interno."
                            );
                        }
                    }
                });

                // Notificar admins e coordenador
                notificarAdminsECoordenador(
                        ambiente,
                        "Ambiente Restrito para Uso Interno 🔒",
                        "O ambiente '" + ambiente.getNome() + "' agora é restrito apenas para uso interno. " +
                        reservasAtivas.size() + " reserva(s) de estudantes foram canceladas automaticamente."
                );
            }
            // Mudou de TRUE para FALSE -> Apenas notificar que agora está aberto
            else if (soInternosAntigo && !soInternosNovo) {
                notificarAdminsECoordenador(
                        ambiente,
                        "Ambiente Aberto para Todos 🔓",
                        "O ambiente '" + ambiente.getNome() + "' agora está disponível para reservas de todos os usuários, incluindo estudantes."
                );
            }
        }

        // Atualizar o atributo soInternos
        ambiente.setSoInternos(ambienteDTO.isSoInternos());

        // Atualizar a aprovação do ambiente
        ambiente.setAprovacao(ambienteDTO.getAprovacao());
        ambienteRepository.save(ambiente);
    }

    /**
     * Exclui um ambiente com todas as regras de negócio associadas:
     * 1. Cancela todas as reservas ativas (PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO)
     * 2. Notifica hosts e membros das reservas canceladas
     * 3. Remove todos os catálogos associados ao ambiente
     * 4. Registra mensagens nas reservas informando o motivo do cancelamento
     * 5. Exclui o ambiente do sistema
     *
     * @param id o identificador único do ambiente a ser excluído
     * @throws SemResultadosException caso não encontre o ambiente pelo id
     */
    @Transactional
    public void deletar(Long id) {
        Ambiente ambiente = ambienteRepository.findById(id)
                .orElseThrow(() -> new SemResultadosException("Ambiente não encontrado"));

        // Define os status que são considerados "ativos" ou "em processo"
        Set<StatusReserva> statusAtivos = Set.of(
                StatusReserva.PENDENTE,
                StatusReserva.APROVADA,
                StatusReserva.CONFIRMADA,
                StatusReserva.ACONTECENDO
        );

        // Busca todos os catálogos do ambiente
        Set<Catalogo> catalogos = catalogoRepository.findCatalogoByAmbienteId(id);

        // Itera sobre todos os catálogos para buscar suas reservas
        for (Catalogo catalogo : catalogos) {
            Set<Reserva> reservasDoAmbiente = reservaRepository.findAllByCatalogo_Id(catalogo.getId());

            // Processa cada reserva do ambiente
            for (Reserva reserva : reservasDoAmbiente) {
                // Cancela apenas reservas com status ativo
                if (statusAtivos.contains(reserva.getStatusReserva())) {

                    // Registra o motivo do cancelamento
                    reserva.setStatusReserva(StatusReserva.CANCELADA);
                    reserva.setFinalidade("A reserva foi cancelada porque o ambiente '" + ambiente.getNome() + "' foi removido do sistema.");

                    // Salva a reserva com as alterações
                    reservaRepository.save(reserva);

                    // Notifica o host (criador da reserva)
                    notificacaoService.novaNotificacao(
                            reserva.getHost(),
                            "Sua reserva com código " + reserva.getCodigo() + " foi cancelada.",
                            "O ambiente '" + ambiente.getNome() + "' foi removido do sistema. Sua reserva foi cancelada automaticamente."
                    );

                    // Notifica todos os membros (participantes) da reserva
                    for (Usuario membro : reserva.getMembros()) {
                        notificacaoService.novaNotificacao(
                                membro,
                                "Uma reserva de que você é participante foi cancelada.",
                                "A reserva com código " + reserva.getCodigo() + " no ambiente '" + ambiente.getNome() + "' foi cancelada porque o ambiente foi removido."
                        );
                    }
                }
            }
        }

        // Remove todos os catálogos do ambiente
        catalogoRepository.deleteAll(catalogos);

        // Remove o ambiente do sistema
        ambienteRepository.delete(ambiente);
    }


    @Transactional
    public void atribuirResponsavel(Long ambienteId, Long responsavelId) {
        Ambiente ambiente = ambienteRepository.findById(ambienteId)
                .orElseThrow(() -> new SemResultadosException("Ambiente não encontrado"));

        Usuario responsavel = usuarioRepository.findById(responsavelId)
                .orElseThrow(() -> new SemResultadosException("Responsável não encontrado"));

        if (!responsavel.getRoles().stream()
                .anyMatch(role -> role.getRoleNome() == Role.Values.COORDENADOR)) {
            throw new SemPermissaoException("Responsável deve ter role COORDENADOR");
        }

        ambiente.setResponsavel(responsavel);
        ambienteRepository.save(ambiente);
    }

    @Transactional
    public void indisponibilizarAmbiente(Long ambienteId) {
        // Buscar ambiente
        Ambiente ambiente = ambienteRepository.findById(ambienteId)
                .orElseThrow(() -> new SemResultadosException("Ambiente não encontrado"));

        // Marcar ambiente como indisponível
        ambiente.setDisponibilidade(Disponibilidade.INDISPONIVEL);
        ambienteRepository.save(ambiente);

        // Buscar todos os catálogos do ambiente
        Set<Catalogo> catalogos = catalogoRepository.findCatalogoByAmbienteId(ambienteId);
        catalogos.forEach(catalogo -> {
            // Marcar catálogos como indisponíveis
            catalogo.setDisponibilidade(Disponibilidade.INDISPONIVEL);
            catalogoRepository.save(catalogo);
        });

        // Buscar todas as reservas do ambiente
        Set<Reserva> reservasDoAmbiente = reservaRepository.findAllByCatalogo_Ambiente_Id(ambienteId);
        reservasDoAmbiente.forEach(reserva -> {
            // Cancelar apenas reservas com status PENDENTE
            if (reserva.getStatusReserva() == StatusReserva.PENDENTE) {
                reserva.setStatusReserva(StatusReserva.CANCELADA);
                reservaRepository.save(reserva);
            }
        });
    }

    @Transactional
    public void disponibilizarAmbiente(Long ambienteId) {
        // Buscar ambiente
        Ambiente ambiente = ambienteRepository.findById(ambienteId)
                .orElseThrow(() -> new SemResultadosException("Ambiente não encontrado"));

        // Marcar ambiente como disponível
        ambiente.setDisponibilidade(Disponibilidade.DISPONIVEL);
        ambienteRepository.save(ambiente);

        // Buscar todos os catálogos do ambiente
        Set<Catalogo> catalogos = catalogoRepository.findCatalogoByAmbienteId(ambienteId);
        catalogos.forEach(catalogo -> {
            // Marcar catálogos como disponíveis
            catalogo.setDisponibilidade(Disponibilidade.DISPONIVEL);
            catalogoRepository.save(catalogo);
        });
    }

    /**
     * Notifica todos os administradores e o coordenador responsável pelo ambiente
     *
     * @param ambiente o ambiente relacionado à notificação
     * @param titulo   o título da notificação
     * @param mensagem a mensagem da notificação
     */
    private void notificarAdminsECoordenador(Ambiente ambiente, String titulo, String mensagem) {
        // Buscar todos os admins
        Set<Usuario> admins = usuarioRepository.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(role -> role.getRoleNome() == Role.Values.ADMIN))
                .collect(Collectors.toSet());

        // Notificar cada admin
        for (Usuario admin : admins) {
            notificacaoService.novaNotificacao(admin, titulo, mensagem);
        }

        // Notificar coordenador do ambiente se houver
        if (ambiente.getResponsavel() != null) {
            notificacaoService.novaNotificacao(ambiente.getResponsavel(), titulo, mensagem);
        }
    }
}