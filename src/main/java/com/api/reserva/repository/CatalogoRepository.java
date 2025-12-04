package com.api.reserva.repository;

import com.api.reserva.entity.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {
    Set<Catalogo> findCatalogoByAmbienteId(Long id);

    Set<Catalogo> findByAmbienteId(Long ambienteId);

}


