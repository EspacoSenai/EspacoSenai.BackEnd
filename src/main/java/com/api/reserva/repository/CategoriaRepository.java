package com.api.reserva.repository;

import com.api.reserva.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    //verifica se existe uma categoria com o nome passado (usado para cadastro)
    boolean existsByNome(String name);
    //verifica se existe uma categoria com o nome passado e id diferente do passado,
    //excluindo a categoria atual da verificação (usado para atualização)
    boolean existsByNomeAndIdNot(String nome, Long id);


}
