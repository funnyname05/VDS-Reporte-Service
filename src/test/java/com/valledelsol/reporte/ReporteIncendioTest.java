package com.valledelsol.reporte;

import com.valledelsol.reporte.domain.CrearReporteDTO;
import com.valledelsol.reporte.domain.ReporteIncendio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReporteIncendio — pruebas unitarias")
class ReporteIncendioTest {

    private CrearReporteDTO dto;
    private ReporteIncendio reporte;

    @BeforeEach
    void setUp() {
        dto = new CrearReporteDTO(
                LocalDate.of(2026, 6, 15),
                LocalTime.of(10, 30, 0),
                "Av. Principal 123",
                "Centro",
                "Frente al parque",
                "Incendio de prueba",
                -33.686876,
                -71.219543
        );
        reporte = new ReporteIncendio(dto, 1L);
        ReflectionTestUtils.setField(reporte, "id", 1L);
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("constructor debe asignar estado ACTIVO por defecto")
    void constructor_debeAsignarEstadoActivoPorDefecto() {
        assertThat(reporte.getEstado()).isEqualTo("ACTIVO");
    }

    @Test
    @DisplayName("constructor debe asignar el usuarioId correctamente")
    void constructor_debeAsignarUsuarioId() {
        assertThat(reporte.getUsuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("constructor debe asignar fecha correctamente")
    void constructor_debeAsignarFecha() {
        assertThat(reporte.getFecha()).isEqualTo(LocalDate.of(2026, 6, 15));
    }

    @Test
    @DisplayName("constructor debe asignar hora correctamente")
    void constructor_debeAsignarHora() {
        assertThat(reporte.getHora()).isEqualTo(LocalTime.of(10, 30, 0));
    }

    @Test
    @DisplayName("constructor debe asignar direccion correctamente")
    void constructor_debeAsignarDireccion() {
        assertThat(reporte.getDireccion()).isEqualTo("Av. Principal 123");
    }

    @Test
    @DisplayName("constructor debe asignar sector correctamente")
    void constructor_debeAsignarSector() {
        assertThat(reporte.getSector()).isEqualTo("Centro");
    }

    @Test
    @DisplayName("constructor debe asignar latitud y longitud correctamente")
    void constructor_debeAsignarCoordenadas() {
        assertThat(reporte.getLatitud()).isEqualTo(-33.686876);
        assertThat(reporte.getLongitud()).isEqualTo(-71.219543);
    }

    // ── actualizarEstado ──────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarEstado debe cambiar el estado correctamente")
    void actualizarEstado_debeCambiarEstado() {
        reporte.actualizarEstado("CONTROLADO");
        assertThat(reporte.getEstado()).isEqualTo("CONTROLADO");
    }

    @Test
    @DisplayName("actualizarEstado a EXTINGUIDO debe funcionar correctamente")
    void actualizarEstado_aExtinguido_debeFuncionar() {
        reporte.actualizarEstado("EXTINGUIDO");
        assertThat(reporte.getEstado()).isEqualTo("EXTINGUIDO");
    }

    @Test
    @DisplayName("actualizarEstado múltiples veces debe conservar el último estado")
    void actualizarEstado_multiplesVeces_debeConservarUltimo() {
        reporte.actualizarEstado("CONTROLADO");
        reporte.actualizarEstado("EXTINGUIDO");
        assertThat(reporte.getEstado()).isEqualTo("EXTINGUIDO");
    }

    // ── equals ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("dos reportes con mismo ID deben ser iguales")
    void equals_conMismoId_debenSerIguales() {
        var otroReporte = new ReporteIncendio(dto, 2L);
        ReflectionTestUtils.setField(otroReporte, "id", 1L);
        assertThat(reporte).isEqualTo(otroReporte);
    }

    @Test
    @DisplayName("dos reportes con distinto ID deben ser diferentes")
    void equals_conDistintoId_debenSerDiferentes() {
        var otroReporte = new ReporteIncendio(dto, 1L);
        ReflectionTestUtils.setField(otroReporte, "id", 2L);
        assertThat(reporte).isNotEqualTo(otroReporte);
    }
}
