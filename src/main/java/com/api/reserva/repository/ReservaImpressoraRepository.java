package com.api.reserva.repository;

import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.ReservaImpressora;
import com.api.reserva.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReservaImpressoraRepository extends JpaRepository<ReservaImpressora, Long> {
//    Set<Usuario> findAllByHost(Usuario host);

    /*
     *Retorna as reservas associadas a um ambiente específico, identificado pelo ID do ambiente no catálogo.
     * @param catalogoAmbienteId o ID do ambiente no catálogo.
     * @return um conjunto de reservas associadas ao ambiente especificado.
     * */
    Set<ReservaImpressora> findAllByHost_Id(Long hostId);



    // Busca reservas cujo host está em hostIds, com data maior que a fornecida e cujo status esteja em 'statuses'
    Set<ReservaImpressora> findAllByHost_IdInAndDataGreaterThanAndStatusReservaIn(Set<Long> hostIds, LocalDate data, Set<StatusReserva> statuses);


    Set<ReservaImpressora> findAllByDataAndStatusReserva(LocalDate dataReserva, StatusReserva statusReserva);

    List<ReservaImpressora> findAllByStatusReservaAndDataBefore(StatusReserva statusReserva, LocalDate localDate);

    List<ReservaImpressora> findAllByStatusReserva(StatusReserva statusReserva);

    Set<ReservaImpressora> findAllByData(LocalDate dataDaReserva);

    Set<ReservaImpressora> findAllByHost_idAndData(Long hostId, LocalDate dataReserva);


    @Query(
            "SELECT r " +
                    "FROM ReservaImpressora r " +
                    "WHERE r.pin = :pin"
    )
    ReservaImpressora findByPin(@Param("pin") Integer pin);


    @Query("SELECT MAX(r.pin) FROM ReservaImpressora r")
    Integer findMaxPin();

}

