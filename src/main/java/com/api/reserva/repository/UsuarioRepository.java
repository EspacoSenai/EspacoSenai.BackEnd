package com.api.reserva.repository;

import com.api.reserva.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Verifica se existe um usuário com o email ou telefone fornecido.
     * Usado para validação de duplicidade no cadastro.
     */
    boolean existsByEmailOrTelefone(String email, String telefone);

    /**
     * Verifica se existe um usuário com o email ou telefone fornecido. Usado para validação de duplicidade na atualização.
     */
    @Query("SELECT COUNT(u) > 0 " +
            "FROM Usuario u " +
            "WHERE u.id <> :id " +
            "AND ((:email is not null AND u.email = :email) " +
            "     OR (:telefone is not null AND u.telefone = :telefone))")
    boolean existsByEmailOrTelefoneAndIdNot(@Param("email") String email,
                                            @Param("telefone") String telefone,
                                            @Param("id") Long id);

    @Query("SELECT u " +
            "FROM Usuario u" +
            " WHERE u.role IN  ('ADMIN', 'COORDENADOR')"
    )
    List<Usuario> findByResponsaveis();
}
