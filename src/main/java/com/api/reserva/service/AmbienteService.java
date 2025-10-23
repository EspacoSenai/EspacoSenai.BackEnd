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
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.MetodosAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<AmbienteReferenciaDTO> buscar() {
        List<Ambiente> ambientes = ambienteRepository.findAll();
        return ambientes.stream()
                .map(AmbienteReferenciaDTO::new)
                .toList();
    }

    /**
     * Salva um novo ambiente
     *
     * @param ambienteDTO os dados do ambiente
     * @return AmbienteDTO com os dados registrados
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
        ambiente.setQtdPessoas(ambienteDTO.getQtdPessoas());
        ambiente.setEmUso(false);


        if (ambienteDTO.getResponsaveisIds() != null) {
            Set<Usuario> responsaveis = usuarioRepository.findAllById(ambienteDTO.getResponsaveisIds())
                    .stream()
                    .filter(usuario -> usuario.getRoles()
                            .stream()
                            .anyMatch(role -> role.getRoleNome() == Role.Values.COORDENADOR))
                    .collect(Collectors.toSet());
            ambiente.setResponsaveis(responsaveis);
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

        Long responsavelId = MetodosAuth.extrairId(authentication);
        Usuario responsavel = usuarioRepository.findById(responsavelId)
                .orElseThrow(() -> new SemResultadosException("Usuário do token"));

        if(!responsavel.getRoles().contains(Role.Values.ADMIN) || !ambiente.getResponsaveis().contains(responsavel)) {
            throw new SemPermissaoException();
        }

        ambiente.setNome(ambienteDTO.getNome());
        ambiente.setDescricao(ambienteDTO.getDescricao());

        // Se disponibilidade mudou para indisponível -> cancelar reservas aprovadas ou pendentes
        if (ambiente.getDisponibilidade() != ambienteDTO.getDisponibilidade() &&
                ambienteDTO.getDisponibilidade() == Disponibilidade.INDISPONIVEL) {
            Set<Reserva> reservasDoAmbiente = reservaRepository.findAllByCatalogo_Ambiente_Id(id);
            reservasDoAmbiente.forEach(reserva -> {
                if (reserva.getStatusReserva().equals(StatusReserva.APROVADA) || reserva.getStatusReserva().equals(StatusReserva.PENDENTE)) {
                    reserva.setStatusReserva(StatusReserva.CANCELADA);
                    reserva.setMsgInterna("O ambiente ficou indisponível.");
                }
            });
        }

        // Se tipo de aprovação mudou e ambiente está disponível -> aprovar pendentes quando passar para automática
        else if (ambiente.getAprovacao() != ambienteDTO.getAprovacao()
                && ambienteDTO.getDisponibilidade() == Disponibilidade.DISPONIVEL) {
            if (ambienteDTO.getAprovacao() == Aprovacao.AUTOMATICA) {
                Set<Reserva> reservasDoAmbiente = reservaRepository.findAllByCatalogo_Ambiente_Id(id);
                reservasDoAmbiente.forEach(reserva -> {
                    StatusReserva status = reserva.getStatusReserva();
                    if (status == StatusReserva.PENDENTE) {
                        reserva.setStatusReserva(StatusReserva.APROVADA);
                        reserva.setMsgInterna("Aprovação automática realizada.");
                    }
                });
            }
        }
        if(ambiente.getQtdPessoas() != ambienteDTO.getQtdPessoas()) {
            Set<Reserva> reservasDoAmbiente = reservaRepository.findAllByCatalogo_Ambiente_Id(id);
            reservasDoAmbiente.forEach(reserva -> {
                // +1 para incluir o solicitante
                int totalPessoas = reserva.getConvidados().size() + 1;
                if(totalPessoas > ambienteDTO.getQtdPessoas() &&
                   (reserva.getStatusReserva() == StatusReserva.APROVADA ||
                    reserva.getStatusReserva() == StatusReserva.PENDENTE)) {
                    reserva.setStatusReserva(StatusReserva.CANCELADA);
                    reserva.setMsgInterna("A quantidade de pessoas do ambiente foi reduzida.");
                }
            });
        }
        ambienteRepository.save(ambiente);

    }    /**
     * Exclui um ambiente
     *
     * @param id o identificador unico do ambiente a ser excluido
     * @throws SemResultadosException caso não encontre o ambiente pelo id
     */
    @Transactional
    public void deletar(Long id) {
        Ambiente ambiente = ambienteRepository.findById(id).orElseThrow(() -> new SemResultadosException("ambiente"));
        ambienteRepository.delete(ambiente);
    }


    @Transactional
    public void associarResponsaveis(Long ambienteId, Set<Long> responsaveisIds) {
        Ambiente ambiente = ambienteRepository.findById(ambienteId).orElseThrow(() -> new SemResultadosException("Ambiente", "associação"));

        Set<Usuario> usuarios = responsaveisIds.stream()
                .map(id -> usuarioRepository.findById(id).orElseThrow(()
                        -> new SemResultadosException("Usuário com o ID: " + id))).collect(Collectors.toSet());

//        for(Usuario usuario : usuarios) {
//            if(!usuario.getRole().equals(UsuarioRole.COORDENADOR) || !usuario.getRole().equals(UsuarioRole.ADMIN)) {
//                throw new SemResultadosException("Usuário não possui role permitida para associação");
//            }
//        }

        ambiente.setResponsaveis(usuarios);

        ambienteRepository.save(ambiente);
    }
}