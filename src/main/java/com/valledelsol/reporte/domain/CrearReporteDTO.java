package com.valledelsol.reporte.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CrearReporteDTO(
        @NotNull
        LocalDate fecha,
        
        @NotNull
        @JsonFormat(pattern = "HH:mm[:ss]")
        LocalTime hora,
        
        @NotBlank
        String direccion,
        
        @NotBlank
        String sector,
        
        String referencia,

        String observaciones,

        Double latitud,    // ← agregar

        Double longitud
) {
}
