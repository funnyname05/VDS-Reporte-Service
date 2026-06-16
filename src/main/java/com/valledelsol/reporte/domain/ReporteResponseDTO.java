package com.valledelsol.reporte.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReporteResponseDTO(
        Long id,
        LocalDate fecha,
        LocalTime hora,
        String direccion,
        String sector,
        String referencia,
        String observaciones,
        String estado,
        Long usuarioId  // el frontend usa este ID para consultar el nombre si lo necesita
) {
    public ReporteResponseDTO(ReporteIncendio reporte) {
        this(
                reporte.getId(),
                reporte.getFecha(),
                reporte.getHora(),
                reporte.getDireccion(),
                reporte.getSector(),
                reporte.getReferencia(),
                reporte.getObservaciones(),
                reporte.getEstado(),
                reporte.getUsuarioId()
        );
    }
}