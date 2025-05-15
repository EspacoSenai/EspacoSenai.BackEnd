package com.api.reserva.service;

import com.api.reserva.dto.AmbienteDTO;
import com.api.reserva.entity.Ambiente;
import com.api.reserva.exception.DadoDuplicadoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.AmbienteRepository;
import com.api.reserva.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

    /**
     * Busca um ambiente específico pelo seu ID
     *
     * @param id identificador único do ambiente
     * @return o DTO do ambiente encontrado
     * @throws SemResultadosException se nenhum ambiente com id fornecido for encontraodo
     */
    public AmbienteDTO listar(Long id) {
        return new AmbienteDTO(ambienteRepository.findById(id).orElseThrow(SemResultadosException::new));
    }

    /**
     * Busca todos os ambientes
     *
     * @return uma lista de DTOs de todos os ambientes
     */
    public List<AmbienteDTO> listar() {
        List<Ambiente> ambientes = ambienteRepository.findAll();
        return ambientes.stream()
                .map(AmbienteDTO::new)
                .toList();
    }

    /**
     * Salva um novo ambiente
     *
     * @param ambienteDTO os dados do ambiente
     * @return AmbienteDTO com os dados registradosç
     * @throws DadoDuplicadoException caso já haja um dado unico existente
     */
    @Transactional
    public AmbienteDTO salvar(AmbienteDTO ambienteDTO) {
        Ambiente ambiente = new Ambiente(ambienteDTO);

        if (ambienteDTO.getCategorias() != null) {
            ambiente.setCategorias(ambienteDTO.getCategorias()
                    .stream()
                    .map(categoriaId -> categoriaRepository.findById(categoriaId)
                            .orElseThrow(() -> new SemResultadosException(String
                                    .format("associação com Id: %s.", categoriaId))))
                    .collect(Collectors.toSet()));
        }
        return new AmbienteDTO(ambienteRepository.save(ambiente));
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
    public AmbienteDTO atualizar(Long id, AmbienteDTO ambienteDTO) {
        Ambiente ambiente = ambienteRepository.findById(id).orElseThrow(() -> new SemResultadosException("atualização"));


        if (!Objects.equals(ambiente.getNome(), ambienteDTO.getNome())) {
            ambiente.setNome(ambienteDTO.getNome());
        }

        if (!Objects.equals(ambiente.getDisponibilidade(), ambienteDTO.getDisponibilidade())) {
            ambiente.setDisponibilidade(ambienteDTO.getDisponibilidade());
        }

        if (!Objects.equals(ambiente.getAprovacao(), ambienteDTO.getAprovacao())) {
            ambiente.setAprovacao(ambienteDTO.getAprovacao());
        }

        if (ambienteDTO.getCategorias() != null) {
            ambiente.setCategorias(ambienteDTO.getCategorias()
                    .stream()
                    .map(categoriaId -> categoriaRepository.findById(categoriaId)
                            .orElseThrow(() -> new SemResultadosException(String.format("associação com Id: %s.", categoriaId))))
                    .collect(Collectors.toSet()));
        }

        return new AmbienteDTO(ambienteRepository.save(ambiente));
    }

    /**
     * Exclui um ambiente
     *
     * @param id o identificador unico do ambiente a ser excluido
     * @throws SemResultadosException caso não encontre o ambiente pelo id
     */
    @Transactional
    public void excluir(Long id) {
        Ambiente ambiente = ambienteRepository.findById(id).orElseThrow(() -> new SemResultadosException("exclusão"));
        ambienteRepository.delete(ambiente);
    }
}