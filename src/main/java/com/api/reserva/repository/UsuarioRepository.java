package com.api.reserva.repository;

import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Verifica se existe um usuário com o email.
     * Usado para validação de duplicidade no cadastro.
     */
    boolean existsByEmail(String email);
    Usuario findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email. Usado para validação de duplicidade na atualização.
     */
    @Query("SELECT COUNT(u) > 0 " +
            "FROM Usuario u " +
            "WHERE u.id <> :id " +
            "AND (:email is not null AND u.email = :email) ")
    boolean existsByEmailAndIdNot(@Param("email") String email,
                                            @Param("id") Long id);

//    @Query("SELECT u " +
//            "FROM Usuario u" +
//            " WHERE u.role IN  ('ADMIN', 'COORDENADOR')"
//    )
//    List<Usuario> findByResponsaveis();

    @Query(
            "SELECT u " +
                    "FROM Usuario u " +
                    "WHERE u.email = :identificador"
    )
    Usuario findByIdentificador(@Param("identificador") String identificador);


//    @Query(
//            "SELECT u " +
//                    "FROM Usuario u " +
//                    "WHERE 'ESTUDANTE' MEMBER OF u.roles"
//    )
//    List<Usuario> findByRoleEstudante();

    Set<Usuario> findAllByRolesRoleNome(Role.Values roleNome);

}
