package com.api.reserva.repository;

import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.ReservaImpressora;
import com.api.reserva.entity.Usuario;
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

    @Query(
            "SELECT r " +
                    "FROM ReservaImpressora r " +
                    "WHERE r.pin = :pin"
    )
    ReservaImpressora findByPin(@Param("pin") Integer pin);


    @Query("SELECT MAX(r.pin) FROM ReservaImpressora r")
    Integer findMaxPin();

    List<ReservaImpressora> findAllByHost_Id(Usuario hostId);

}