package com.valledelsol.reporte;

import com.valledelsol.reporte.domain.CrearReporteDTO;
import com.valledelsol.reporte.domain.ReporteIncendio;
import com.valledelsol.reporte.domain.ReporteResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReporteResponseDTO — pruebas unitarias")
class ReporteResponseDTOTest {

    private ReporteIncendio reporte;
    private ReporteResponseDTO dto;

    @BeforeEach
    void setUp() {
        var crearDTO = new CrearReporteDTO(
                LocalDate.of(2026, 6, 15),
                LocalTime.of(10, 30, 0),
                "Av. Principal 123",
                "Centro",
                "Frente al parque",
                "Incendio de prueba",
                -33.686876,
                -71.219543
        );
        reporte = new ReporteIncendio(crearDTO, 1L);
        ReflectionTestUtils.setField(reporte, "id", 1L);
        dto = new ReporteResponseDTO(reporte);
    }

    @Test
    @DisplayName("debe mapear el ID correctamente")
    void debeMappearId() {
        assertThat(dto.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("debe mapear la fecha correctamente")
    void debeMappearFecha() {
        assertThat(dto.fecha()).isEqualTo(LocalDate.of(2026, 6, 15));
    }

    @Test
    @DisplayName("debe mapear la hora correctamente")
    void debeMappearHora() {
        assertThat(dto.hora()).isEqualTo(LocalTime.of(10, 30, 0));
    }

    @Test
    @DisplayName("debe mapear la dirección correctamente")
    void debeMappearDireccion() {
        assertThat(dto.direccion()).isEqualTo("Av. Principal 123");
    }

    @Test
    @DisplayName("debe mapear el sector correctamente")
    void debeMappearSector() {
        assertThat(dto.sector()).isEqualTo("Centro");
    }

    @Test
    @DisplayName("debe mapear el estado ACTIVO por defecto")
    void debeMappearEstadoActivo() {
        assertThat(dto.estado()).isEqualTo("ACTIVO");
    }

    @Test
    @DisplayName("debe mapear el usuarioId correctamente")
    void debeMappearUsuarioId() {
        assertThat(dto.usuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("debe reflejar el estado actualizado del reporte")
    void debeReflejarEstadoActualizado() {
        reporte.actualizarEstado("CONTROLADO");
        var dtoActualizado = new ReporteResponseDTO(reporte);
        assertThat(dtoActualizado.estado()).isEqualTo("CONTROLADO");
    }
}
