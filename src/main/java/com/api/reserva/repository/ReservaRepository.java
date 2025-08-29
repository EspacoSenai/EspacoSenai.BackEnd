package com.api.reserva.repository;

import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Set<Usuario> findAllByHost(Usuario host);

}
