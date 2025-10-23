package com.api.reserva.repository;

import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    Set<Reserva> findByCatalogo_Id(Long catalogoId);
}
