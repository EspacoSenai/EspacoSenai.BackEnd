package com.api.reserva.repository;

import com.api.reserva.entity.GradeAmbiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeAmbienteRepository extends JpaRepository<GradeAmbiente, Long> {
}
