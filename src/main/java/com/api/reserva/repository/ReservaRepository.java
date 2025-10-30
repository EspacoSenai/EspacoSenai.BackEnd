package com.api.reserva.repository;

import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
//    Set<Usuario> findAllByHost(Usuario host);

    /*
     *Retorna as reservas associadas a um ambiente específico, identificado pelo ID do ambiente no catálogo.
     * @param catalogoAmbienteId o ID do ambiente no catálogo.
     * @return um conjunto de reservas associadas ao ambiente especificado.
     * */
    Set<Reserva> findAllByCatalogo_Ambiente_Id(Long catalogoAmbienteId);
    Set<Reserva> findAllByCatalogo_Id(Long catalogoId);
    Set<Reserva> findAllByHost_Id(Long hostId);

    Set<Reserva> findByCatalogo_Id(Long catalogoId);

    // Busca reservas cujo host está em hostIds, com data maior que a fornecida e cujo status esteja em 'statuses'
    Set<Reserva> findAllByHost_IdInAndDataGreaterThanAndStatusReservaIn(Set<Long> hostIds, LocalDate data, Set<StatusReserva> statuses);

    // Busca reservas onde o usuário é membro/participante
    Set<Reserva> findAllByMembros_Id(Long membroId);

    // Busca reservas onde o usuário é membro/participante com status em conjunto
    Set<Reserva> findAllByMembros_IdAndStatusReservaIn(Long membroId, Set<StatusReserva> statuses);
}
