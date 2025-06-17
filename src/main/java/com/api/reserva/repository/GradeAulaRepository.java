package com.api.reserva.repository;

import com.api.reserva.entity.GradeAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeAulaRepository extends JpaRepository<GradeAula, Long> {
}
