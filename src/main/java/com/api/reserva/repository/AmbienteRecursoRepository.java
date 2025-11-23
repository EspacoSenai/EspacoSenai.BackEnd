package com.api.reserva.repository;

import com.api.reserva.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbienteRecursoRepository extends JpaRepository<Recurso, Long> {
}
