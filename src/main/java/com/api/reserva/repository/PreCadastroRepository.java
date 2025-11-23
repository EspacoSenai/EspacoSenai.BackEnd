package com.api.reserva.repository;

import com.api.reserva.entity.PreCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreCadastroRepository extends JpaRepository<PreCadastro, Long> {

//    @Query(
//            "SELECT u " +
//                    "FROM Usuario u " +
//                    "WHERE u.email = :identificador OR u.telefone = :identificador"
//    )
//

    PreCadastro findByEmail(String email);

    boolean existsByEmail(String email);
}
