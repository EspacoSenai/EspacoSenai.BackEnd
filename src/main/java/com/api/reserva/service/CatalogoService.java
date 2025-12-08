package com.api.reserva.service;

import com.api.reserva.dto.CatalogoDTO;
import com.api.reserva.dto.CatalogoReferenciaDTO;
import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Catalogo;
import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.AmbienteRepository;
import com.api.reserva.repository.CatalogoRepository;
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.MetodosAuth;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CatalogoService {
    @Autowired
    public CatalogoRepository catalogoRepository;
    @Autowired
    public AmbienteRepository ambienteRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private NotificacaoService notificacaoService;

    public List<CatalogoReferenciaDTO> buscar() {
        return catalogoRepository.findAll().stream()
                .map(CatalogoReferenciaDTO::new)
                .toList();
    }

    public CatalogoReferenciaDTO buscar(Long id) {
        return new CatalogoReferenciaDTO(catalogoRepository.findById(id).orElseThrow(SemResultadosException::new));
    }

    public Set<CatalogoReferenciaDTO> buscarPorAmbienteId(Long ambienteId) {
        Set<Catalogo> catalogos = catalogoRepository.findByAmbienteId(ambienteId);
        return catalogos.stream()
                .map(CatalogoReferenciaDTO::new)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void salvar(Long ambienteId, Set<CatalogoDTO> catalogosDTO, Authentication authentication) {
        Ambiente ambiente = ambienteRepository.findById(ambienteId).orElseThrow(() ->
                new SemResultadosException("vinculação de Ambiente."));

        Long usuarioId = MetodosAuth.extrairId(authentication);
        boolean ehAdmin = MetodosAuth.extrairRole(authentication).contains("SCOPE_ADMIN");
        boolean ehResponsavel = ambiente.getResponsavel() != null &&
                ambiente.getResponsavel().getId().equals(usuarioId);

        if (!ehAdmin && !ehResponsavel) {
            throw new SemPermissaoException("Apenas ADMIN ou o responsável podem salvar catálogos");
        }

        Set<Catalogo> catalogos = new HashSet<>(catalogoRepository.findCatalogoByAmbienteId(ambiente.getId()));


        catalogosDTO.forEach(catalogoDTO -> {
            ValidacaoDatasEHorarios.validarHorarios(catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim());
            for (Catalogo c : catalogos) {
                if (Objects.equals(catalogoDTO.getDiaSemana(), c.getDiaSemana())) {
                    ValidacaoDatasEHorarios.validarCatalogo(
                            catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim(),
                            c.getHoraInicio(), c.getHoraFim());
                }
            }
            Catalogo catalogo = new Catalogo(
                    ambiente,
                    catalogoDTO.getHoraInicio(),
                    catalogoDTO.getHoraFim(),
                    catalogoDTO.getDiaSemana(),
                    catalogoDTO.getDisponibilidade()
            );

            catalogos.add(catalogo);
        });
        catalogoRepository.saveAll(catalogos);
    }

    @Transactional
    public void atualizar(Long catalogoId, CatalogoDTO catalogoDTO, Authentication authentication) {
        Ambiente ambiente = ambienteRepository.findById(catalogoDTO.getAmbienteId()).orElseThrow(() ->
                new SemResultadosException("Ambiente"));

        Long usuarioId = MetodosAuth.extrairId(authentication);
        boolean ehAdmin = MetodosAuth.extrairRole(authentication).contains("SCOPE_ADMIN");
        boolean ehResponsavel = ambiente.getResponsavel() != null &&
                ambiente.getResponsavel().getId().equals(usuarioId);

        if (!ehAdmin && !ehResponsavel) {
            throw new SemPermissaoException("Apenas ADMIN ou o responsável podem atualizar catálogos");
        }

        Catalogo catalogoExistente = catalogoRepository.findById(catalogoId).orElseThrow(() ->
                new SemResultadosException("Catálogo"));

        // Verificar se o catálogo pertence ao ambiente
        if (!catalogoExistente.getAmbiente().getId().equals(ambiente.getId())) {
            throw new SemPermissaoException("Catálogo não pertence ao ambiente especificado");
        }

        ValidacaoDatasEHorarios.validarHorarios(catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim());

        boolean diaMudou = !Objects.equals(catalogoDTO.getDiaSemana(), catalogoExistente.getDiaSemana());
        boolean horarioMudou = !Objects.equals(catalogoDTO.getHoraInicio(), catalogoExistente.getHoraInicio()) ||
                !Objects.equals(catalogoDTO.getHoraFim(), catalogoExistente.getHoraFim());
        boolean disponibilidadeMudou = !Objects.equals(catalogoDTO.getDisponibilidade(), catalogoExistente.getDisponibilidade());

        // Validar sobreposição com OUTROS catálogos (não incluir o atual)
        if (diaMudou || horarioMudou) {
            Set<Catalogo> outrosCatalogos = ambiente.getCatalogos().stream()
                    .filter(c -> !Objects.equals(c.getId(), catalogoId))
                    .filter(c -> Objects.equals(c.getDiaSemana(), catalogoDTO.getDiaSemana()))
                    .collect(Collectors.toSet());

            for (Catalogo outro : outrosCatalogos) {
                if (catalogoDTO.getHoraInicio().isBefore(outro.getHoraFim()) &&
                    catalogoDTO.getHoraFim().isAfter(outro.getHoraInicio())) {
                    throw new SemPermissaoException(
                        String.format("Já existe um catálogo no dia %s com horário que se sobrepõe: %s-%s",
                            catalogoDTO.getDiaSemana(), outro.getHoraInicio(), outro.getHoraFim())
                    );
                }
            }
        }

        // Verificar se o catálogo está mudando para INDISPONÍVEL
        boolean mudandoParaIndisponivel = disponibilidadeMudou &&
                catalogoDTO.getDisponibilidade().equals(com.api.reserva.enums.Disponibilidade.INDISPONIVEL);

        // Define os status que são considerados "ativos"
        Set<StatusReserva> statusAtivos = Set.of(
                StatusReserva.PENDENTE,
                StatusReserva.APROVADA,
                StatusReserva.CONFIRMADA,
                StatusReserva.ACONTECENDO
        );

        if (diaMudou || horarioMudou || disponibilidadeMudou) {
            // Buscar APENAS as reservas deste catálogo específico
            Set<Reserva> reservasDoCatalogo = reservaRepository.findAllByCatalogo_Id(catalogoId)
                    .stream()
                    .filter(r -> statusAtivos.contains(r.getStatusReserva()))
                    .collect(Collectors.toSet());

            // Determinar o motivo do cancelamento
            String motivoCancelamento;
            if (mudandoParaIndisponivel) {
                motivoCancelamento = "O catálogo foi marcado como INDISPONÍVEL";
            } else if (diaMudou && horarioMudou) {
                motivoCancelamento = String.format("O dia da semana mudou de %s para %s e o horário mudou de %s-%s para %s-%s",
                        catalogoExistente.getDiaSemana(), catalogoDTO.getDiaSemana(),
                        catalogoExistente.getHoraInicio(), catalogoExistente.getHoraFim(),
                        catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim());
            } else if (diaMudou) {
                motivoCancelamento = String.format("O dia da semana mudou de %s para %s",
                        catalogoExistente.getDiaSemana(), catalogoDTO.getDiaSemana());
            } else if (horarioMudou) {
                motivoCancelamento = String.format("O horário mudou de %s-%s para %s-%s",
                        catalogoExistente.getHoraInicio(), catalogoExistente.getHoraFim(),
                        catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim());
            } else {
                motivoCancelamento = String.format("A disponibilidade mudou de %s para %s",
                        catalogoExistente.getDisponibilidade(), catalogoDTO.getDisponibilidade());
            }

            // Cancelar reservas ativas e notificar usuários
            if (!reservasDoCatalogo.isEmpty()) {
                String msgCatalogoInfo = String.format("%s %s-%s",
                        catalogoExistente.getDiaSemana(),
                        catalogoExistente.getHoraInicio(),
                        catalogoExistente.getHoraFim());

                reservasDoCatalogo.forEach(reserva -> {
                    // Atualizar finalidade da reserva com o motivo
                    if (reserva.getFinalidade() != null && !reserva.getFinalidade().isEmpty()) {
                        reserva.setFinalidade(reserva.getFinalidade() +
                                " [Atualização: " + motivoCancelamento + "]");
                    } else {
                        reserva.setFinalidade("Cancelada automaticamente. Motivo: " + motivoCancelamento);
                    }

                    // Cancelar reserva
                    reserva.setStatusReserva(StatusReserva.CANCELADA);
                    reservaRepository.save(reserva);

                    // Notificar host
                    notificacaoService.novaNotificacao(
                            reserva.getHost(),
                            "Reserva Cancelada - Catálogo Alterado ⚠️",
                            "Sua reserva no ambiente '" + ambiente.getNome() +
                            "' (código: " + reserva.getCodigo() + ") foi cancelada.\n" +
                            "Catálogo: " + msgCatalogoInfo + "\n" +
                            "Motivo: " + motivoCancelamento
                    );

                    // Notificar todos os membros
                    for (Usuario membro : reserva.getMembros()) {
                        notificacaoService.novaNotificacao(
                                membro,
                                "Reserva Cancelada - Catálogo Alterado ⚠️",
                                "Uma reserva de que você participa no ambiente '" + ambiente.getNome() +
                                "' (código: " + reserva.getCodigo() + ") foi cancelada.\n" +
                                "Catálogo: " + msgCatalogoInfo + "\n" +
                                "Motivo: " + motivoCancelamento
                        );
                    }
                });
            }

            // Atualizar o catálogo
            catalogoExistente.setDiaSemana(catalogoDTO.getDiaSemana());
            catalogoExistente.setHoraInicio(catalogoDTO.getHoraInicio());
            catalogoExistente.setHoraFim(catalogoDTO.getHoraFim());
            catalogoExistente.setDisponibilidade(catalogoDTO.getDisponibilidade());
            catalogoRepository.save(catalogoExistente);
        }
    }

    /**
     * Exclui múltiplos catálogos com regras de negócio:
     * 1. Cancela todas as reservas ativas vinculadas aos catálogos (PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO)
     * 2. Mantém as mensagens existentes nas reservas e adiciona mensagem sobre exclusão do catálogo
     * 3. Notifica hosts e membros das reservas canceladas
     * 4. Remove os catálogos do sistema
     *
     * @param catalogosIds conjunto de IDs dos catálogos a serem excluídos
     * @param authentication autenticação do usuário que solicitou a exclusão
     * @throws SemPermissaoException caso o usuário não tenha permissão
     * @throws SemResultadosException caso algum catálogo não seja encontrado
     */
    @Transactional
    public void deletar(Set<Long> catalogosIds, Authentication authentication) {
        // Define os status que são considerados "ativos" ou "em processo"
        Set<StatusReserva> statusAtivos = Set.of(
                StatusReserva.PENDENTE,
                StatusReserva.APROVADA,
                StatusReserva.CONFIRMADA,
                StatusReserva.ACONTECENDO
        );

        for (Long id : catalogosIds) {
            Catalogo catalogo = catalogoRepository.findById(id).orElseThrow(
                    () -> new SemResultadosException("catálogo não encontrado"));

            Ambiente ambiente = catalogo.getAmbiente();

            // Validação de permissão
            Long usuarioId = MetodosAuth.extrairId(authentication);
            boolean ehAdmin = MetodosAuth.extrairRole(authentication).contains("SCOPE_ADMIN");
            boolean ehResponsavel = ambiente.getResponsavel() != null &&
                    ambiente.getResponsavel().getId().equals(usuarioId);

            if (!ehAdmin && !ehResponsavel) {
                throw new SemPermissaoException("Apenas ADMIN ou o responsável podem deletar catálogos");
            }

            // Busca todas as reservas vinculadas ao catálogo
            Set<Reserva> reservasDoCatalogo = reservaRepository.findAllByCatalogo_Id(id);

            // Processa cada reserva do catálogo
            for (Reserva reserva : reservasDoCatalogo) {
                // Cancela apenas reservas com status ativo
                if (statusAtivos.contains(reserva.getStatusReserva())) {

                    // Cria mensagem informando sobre a deleção do catálogo
                    String msgDiaSemana = String.format("%s %s-%s",
                            catalogo.getDiaSemana(),
                            catalogo.getHoraInicio(),
                            catalogo.getHoraFim());

                    // Manter mensagem existente e adicionar informação sobre deleção
                    if (reserva.getFinalidade() != null && !reserva.getFinalidade().isEmpty()) {
                        reserva.setFinalidade(reserva.getFinalidade() +
                            " [Atualização: O catálogo " + msgDiaSemana + " foi removido do sistema.]");
                    } else {
                        reserva.setFinalidade("O catálogo " + msgDiaSemana + " do ambiente '" +
                            ambiente.getNome() + "' foi removido do sistema.");
                    }


                    // Altera o status para cancelada
                    reserva.setStatusReserva(StatusReserva.CANCELADA);

                    // Salva a reserva com as alterações
                    reservaRepository.save(reserva);

                    // Notifica o host (criador da reserva)
                    notificacaoService.novaNotificacao(
                            reserva.getHost(),
                            "Sua reserva foi cancelada",
                            "A reserva com código " + reserva.getCodigo() + " foi cancelada porque o catálogo " +
                            msgDiaSemana + " foi removido do ambiente '" + ambiente.getNome() + "'."
                    );

                    // Notifica todos os membros (participantes) da reserva
                    for (Usuario membro : reserva.getMembros()) {
                        notificacaoService.novaNotificacao(
                                membro,
                                "Uma reserva de que você é participante foi cancelada",
                                "A reserva com código " + reserva.getCodigo() + " foi cancelada porque o catálogo " +
                                msgDiaSemana + " do ambiente '" + ambiente.getNome() + "' foi removido."
                        );
                    }
                }
            }
        }

        // Remove os catálogos do sistema
        catalogoRepository.deleteAllById(catalogosIds);
    }
}
