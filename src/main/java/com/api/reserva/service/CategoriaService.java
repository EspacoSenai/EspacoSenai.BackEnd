package com.api.reserva.service;

import com.api.reserva.dto.CategoriaDTO;
import com.api.reserva.entity.Categoria;
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
 * Serviço responsável pelo gerenciamento de operações relacionadas a entidade Categoria.
 */
@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository repository;
    @Autowired
    private AmbienteRepository ambienteRepository;

    /**
     * Busca uma categoria específica pelo seu ID.
     *
     * @param id Identificador único da categoria
     * @return O DTO da categoria encontrada
     * @throws SemResultadosException se nenhuma categoria for encontrada com o ID fornecido
     */
    public CategoriaDTO listar(Long id) {
        return new CategoriaDTO(repository.findById(id).orElseThrow(SemResultadosException::new));
    }

    /**
     * Lista todas as categorias disponíveis no sistema.
     *
     * @return Uma lista de DTOs de todas as categorias
     */
    public List<CategoriaDTO> listar() {
        //retorna todas as categorias
        List<Categoria> categorias = repository.findAll();
        //converte a lista de categorias para uma lista de CategoriaDTO
        return categorias.stream()
                .map(CategoriaDTO::new)
                .toList();
    }

    /**
     * Salva uma nova categoria no sistema.
     *
     * @param categoriaDTO O DTO contendo as informações da categoria a ser salva
     * @return O DTO da categoria salva com ID gerado
     * @throws DadoDuplicadoException se já existir uma categoria com o mesmo nome
     */
    @Transactional
    public CategoriaDTO salvar(CategoriaDTO categoriaDTO) {
        //verifica se já existe uma categoria com o nome passado
        if (repository.existsByNome(categoriaDTO.getNome())) {
            throw new DadoDuplicadoException("Nome");
        }

        Categoria categoria = new Categoria(categoriaDTO);

        if (categoriaDTO.getAmbientes() != null) {
            categoria.setAmbientes(categoriaDTO.getAmbientes()
                    .stream()
                    .map(ambienteId -> ambienteRepository.findById(ambienteId.getId())
                            .orElseThrow(() -> new SemResultadosException(String.format("associação com o Id: %s", ambienteId))))
                    .collect(Collectors.toSet()));
        }

        return new CategoriaDTO(repository.save(categoria));
    }

    /**
     * Atualiza as informações de uma categoria existente.
     *
     * @param id           ID da categoria a ser atualizada
     * @param categoriaDTO DTO contendo as novas informações da categoria
     * @return O DTO da categoria atualizada
     * @throws SemResultadosException se a categoria com o ID especificado não for encontrada
     * @throws DadoDuplicadoException se já existir outra categoria com o mesmo nome
     */
    @Transactional
    public CategoriaDTO atualizar(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoria = repository.findById(id).orElseThrow(() -> new SemResultadosException("atualização"));

        if (repository.existsByNomeAndIdNot(categoriaDTO.getNome(), id)) {
            throw new DadoDuplicadoException("Nome");
        }

        if (!Objects.equals(categoria.getNome(), categoriaDTO.getNome())) {
            categoria.setNome(categoriaDTO.getNome());
        }

        if (categoriaDTO.getAmbientes() != null) {
            categoria.setAmbientes(categoriaDTO.getAmbientes()
                    .stream()
                    .map(ambienteId -> ambienteRepository.findById(ambienteId.getId())
                            .orElseThrow(() -> new SemResultadosException(String.format("associação com o Id: %s", ambienteId))))
                    .collect(Collectors.toSet()));
        }
        return new CategoriaDTO(repository.save(categoria));
    }

    /**
     * Exclui uma categoria do sistema pelo seu ID.
     *
     * @param id ID da categoria a ser removida
     * @throws SemResultadosException se a categoria com o ID especificado não for encontrada
     */
    @Transactional
    public void excluir(Long id) {
        //busca a categoria pelo id, se não existir, lança uma exceção
        Categoria categoria = repository.findById(id).orElseThrow(() -> new SemResultadosException("exclusão"));
        //deleta a categoria do banco
        repository.delete(categoria);
    }
}