package com.api.reserva.repository;

import com.api.reserva.entity.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    List<Turma> findByNome(String nome);
    Optional<Turma> findByCodigoAcesso(String codigoAcesso);

    // Retorna todas as turmas em que um estudante (usuario) está inscrito
    List<Turma> findAllByEstudantes_Id(Long estudanteId);
}
