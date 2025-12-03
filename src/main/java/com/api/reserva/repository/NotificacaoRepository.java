package com.api.reserva.repository;

import com.api.reserva.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    /**
     * Busca todas as notificações de um usuário específico
     *
     * @param usuarioId ID do usuário
     * @return Lista de notificações ordenadas por data decrescente
     */
    @Query("SELECT n FROM Notificacao n WHERE n.usuario.id = :usuarioId ORDER BY n.criadoEm DESC")
    List<Notificacao> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Busca apenas notificações NÃO LIDAS de um usuário
     *
     * @param usuarioId ID do usuário
     * @return Lista de notificações não lidas ordenadas por data decrescente
     */
    @Query("SELECT n FROM Notificacao n WHERE n.usuario.id = :usuarioId AND n.lida = false ORDER BY n.criadoEm DESC")
    List<Notificacao> findNaoLidasByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Conta quantas notificações não lidas um usuário tem
     *
     * @param usuarioId ID do usuário
     * @return Quantidade de notificações não lidas
     */
    @Query("SELECT COUNT(n) FROM Notificacao n WHERE n.usuario.id = :usuarioId AND n.lida = false")
    Integer countNaoLidasByUsuarioId(@Param("usuarioId") Long usuarioId);
}