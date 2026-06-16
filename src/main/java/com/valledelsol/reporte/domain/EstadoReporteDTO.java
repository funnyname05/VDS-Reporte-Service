package com.valledelsol.reporte.domain;

import jakarta.validation.constraints.NotBlank;

public record EstadoReporteDTO(
        @NotBlank
        String estado
) {
}
