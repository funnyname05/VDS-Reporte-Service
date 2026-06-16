package com.valledelsol.reporte.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteIncendioRepository extends JpaRepository<ReporteIncendio, Long> {
    List<ReporteIncendio> findAllByOrderByFechaDescHoraDesc();
}
