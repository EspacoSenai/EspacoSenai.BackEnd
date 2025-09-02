package com.api.reserva.service;

import com.api.reserva.dto.AmbienteDTO;
import com.api.reserva.dto.AmbienteReferenciaDTO;
import com.api.reserva.entity.*;
import com.api.reserva.enums.Aprovacao;
import com.api.reserva.enums.Disponibilidade;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.DadoDuplicadoException;
import com.api.reserva.exception.EntidadeJaExistente;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.AmbienteRepository;
import com.api.reserva.repository.CategoriaRepository;
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    CategoriaRepository categoriaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    /**
     * Busca um ambiente específico pelo seu ID
     *
     * @param id identificador único do ambiente
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

        Ambiente ambiente = new Ambiente(ambienteDTO);

        if (ambienteRepository.existsByNome(ambienteDTO.getNome())) {
            throw new EntidadeJaExistente("Ambiente");
        }

        if (ambienteDTO.getResponsaveisIds() != null) {
            Set<Usuario> responsaveis = usuarioRepository.findAllById(ambienteDTO.getResponsaveisIds())
                    .stream()
                    .filter(usuario -> usuario.getRoles().contains(Role.Values.COORDENADOR))
                    .collect(Collectors.toSet());
            ambiente.setResponsaveis(responsaveis);
        }

        if (ambienteDTO.getCategoriasIds() != null) {
            List<Categoria> categorias = categoriaRepository.findAllById(ambienteDTO.getCategoriasIds());
            ambiente.setCategorias(new HashSet<>(categorias));
        }

//        if (ambienteDTO.getCategorias() != null) {
//            ambiente.setCategorias(ambienteDTO.getCategorias()
//                    .stream()
//                    .map(categoriaId -> categoriaRepository.findById(categoriaId.getId())
//                            .orElseThrow(() -> new SemResultadosException(String
//                                    .format("associação com Id: %s.", categoriaId))))
//                    .collect(Collectors.toSet()));
//        }
        ambienteRepository.save(ambiente);
    }

    /**
     * Atualiza um ambiente
     *
     * @param ambienteDTO os novos dados ao ambiente
     * @param id          identificador do ambiente a ser atualizado
     * @return um DTO contendo os novos dados
     * @throws SemResultadosException caso não encontre o ambiente pelo id
     * @throws DadoDuplicadoException caso já haja um dado unico existente
     */
    @Transactional
    public void atualizar(Long id, AmbienteDTO ambienteDTO) {
        Ambiente ambiente = ambienteRepository.findById(id).orElseThrow(() -> new SemResultadosException("atualização"));

        Set<Reserva> reservasDoAmbiente = reservaRepository.findAll()
                .stream()
                .filter(reserva -> reserva.getCatalogo().getAmbiente().equals(ambiente))
                .collect(Collectors.toSet());

        ambiente.setNome(ambienteDTO.getNome());
        ambiente.setDescricao(ambienteDTO.getDescricao());
        ambiente.setCategorias(new HashSet<>(categoriaRepository.findAllById(ambienteDTO.getCategoriasIds())));

        /*
         *
         * */
        if (ambienteDTO.getDisponibilidade() == Disponibilidade.INDISPONIVEL) {
            reservasDoAmbiente.forEach(reserva -> {
                reserva.setStatusReserva(StatusReserva.CANCELADA);
                reserva.setMsgInterna("O ambiente ficou indisponível.");
            });
        }

        if (ambiente.getAprovacao() != ambienteDTO.getAprovacao() &&
                ambiente.getDisponibilidade() == Disponibilidade.DISPONIVEL) {
            if (ambienteDTO.getAprovacao() == Aprovacao.AUTOMATICA) {
                reservasDoAmbiente.forEach(reserva -> {
                    reserva.setStatusReserva(StatusReserva.APROVADA);
                    reserva.setMsgInterna("Aprovação automática realizada."); // Adicionado argumento esperado
                });
            }
        }

        reservaRepository.saveAll(reservasDoAmbiente);

        ambienteRepository.save(ambiente);
    }

    /**
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
//
//    @Transactional
//    public void associarCategorias(Long ambienteId, Set<Long> categoriasIds) {
//        Ambiente ambiente = ambienteRepository.findById(ambienteId).orElseThrow(() -> new SemResultadosException("ambiente"));
//
//        ambiente.setCategorias(idsCategorias.stream()
//                .map(idCategoria -> categoriaRepository.findById(idCategoria)
//                        .orElseThrow(() -> new SemResultadosException("associação.")))
//                .collect(Collectors.toSet()));
//    }

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