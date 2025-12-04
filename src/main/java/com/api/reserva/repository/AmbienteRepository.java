package com.api.reserva.repository;

import com.api.reserva.entity.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {
    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);

                             Ambiente findByNome(String nome);

//    /**
//     * Este método retorna uma String derivada de uma Query que leva THEN, identificando o exato campo duplicado.
//     * (cadastro e atualização.)
//     *
//     * @param nome          do ambiente
//     * @param id            do ambiente
//     */
//    //Por mais que o id seja nulo num PUT, havera verificacao precedente como um findById
//    @Query("SELECT CASE " +
//            "WHEN EXISTS(SELECT 1 FROM Ambiente a WHERE a.nome = :nome AND (:id IS NULL OR a.id <> :id)) THEN 'Nome' " +
//            "ELSE null END")
//    String verificarDuplicidade(@Param("nome") String nome,
//                                @Param("id") Long id);
}
