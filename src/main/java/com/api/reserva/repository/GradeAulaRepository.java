package com.api.reserva.repository;

import com.api.reserva.entity.GradeAula;
import com.api.reserva.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeAulaRepository extends JpaRepository<GradeAula, Long> {
    Optional<GradeAula> findBySalaAndDiaAndHorario_Id(String sala, DiaSemana dia, Long idHorario);
}
