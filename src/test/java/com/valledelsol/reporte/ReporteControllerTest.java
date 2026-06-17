package com.valledelsol.reporte;

import com.valledelsol.reporte.controller.ReporteController;
import com.valledelsol.reporte.domain.CrearReporteDTO;
import com.valledelsol.reporte.domain.EstadoReporteDTO;
import com.valledelsol.reporte.domain.ReporteIncendio;
import com.valledelsol.reporte.domain.ReporteIncendioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteController — pruebas unitarias")
class ReporteControllerTest {

    @Mock
    private ReporteIncendioRepository reporteRepository;

    @InjectMocks
    private ReporteController reporteController;

    // JWT real generado con HMAC256 y claim "id":1 para pruebas
    // Header: Bearer + token con claim id=1
    private static final String AUTH_HEADER = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
            ".eyJpc3MiOiJ2YWxsZV9kZWxfc29sIiwic3ViIjoiYWRtaW5AdGVzdC5jb20iLCJpZCI6MSwiZXhwIjo5OTk5OTk5OTk5fQ" +
            ".placeholder";

    private ReporteIncendio reportePrueba;
    private CrearReporteDTO crearReporteDTO;

    @BeforeEach
    void setUp() {
        crearReporteDTO = new CrearReporteDTO(
                LocalDate.of(2026, 6, 15),
                LocalTime.of(10, 30, 0),
                "Av. Principal 123",
                "Centro",
                "Frente al parque",
                "Incendio de prueba",
                -33.686876,
                -71.219543
        );

        reportePrueba = new ReporteIncendio(crearReporteDTO, 1L);
        ReflectionTestUtils.setField(reportePrueba, "id", 1L);
    }

    // ── listarReportes ────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarReportes debe retornar 200 con lista de reportes")
    void listarReportes_debeRetornar200ConLista() {
        when(reporteRepository.findAllByOrderByFechaDescHoraDesc())
                .thenReturn(List.of(reportePrueba));

        var response = reporteController.listarReportes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("listarReportes con BD vacía debe retornar lista vacía")
    void listarReportes_conBDVacia_debeRetornarListaVacia() {
        when(reporteRepository.findAllByOrderByFechaDescHoraDesc())
                .thenReturn(List.of());

        var response = reporteController.listarReportes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("listarReportes debe llamar al repositorio exactamente una vez")
    void listarReportes_debeLlamarRepositorioUnaVez() {
        when(reporteRepository.findAllByOrderByFechaDescHoraDesc())
                .thenReturn(List.of());

        reporteController.listarReportes();

        verify(reporteRepository, times(1)).findAllByOrderByFechaDescHoraDesc();
    }

    // ── obtenerReporte ────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerReporte con ID existente debe retornar 200")
    void obtenerReporte_conIdExistente_debeRetornar200() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reportePrueba));

        var response = reporteController.obtenerReporte(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().direccion()).isEqualTo("Av. Principal 123");
    }

    @Test
    @DisplayName("obtenerReporte con ID inexistente debe retornar 404")
    void obtenerReporte_conIdInexistente_debeRetornar404() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        var response = reporteController.obtenerReporte(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("obtenerReporte debe retornar los datos correctos del reporte")
    void obtenerReporte_debeRetornarDatosCorrectos() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reportePrueba));

        var response = reporteController.obtenerReporte(1L);

        assertThat(response.getBody().sector()).isEqualTo("Centro");
        assertThat(response.getBody().estado()).isEqualTo("ACTIVO");
        assertThat(response.getBody().usuarioId()).isEqualTo(1L);
    }

    // ── actualizarEstadoReporte ───────────────────────────────────────────────

    @Test
    @DisplayName("actualizarEstado con ID existente debe retornar 200")
    void actualizarEstado_conIdExistente_debeRetornar200() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reportePrueba));
        when(reporteRepository.save(any())).thenReturn(reportePrueba);

        var datos = new EstadoReporteDTO("CONTROLADO");
        var response = reporteController.actualizarEstadoReporte(1L, datos);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("actualizarEstado con ID inexistente debe retornar 404")
    void actualizarEstado_conIdInexistente_debeRetornar404() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        var datos = new EstadoReporteDTO("CONTROLADO");
        var response = reporteController.actualizarEstadoReporte(99L, datos);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(reporteRepository, never()).save(any());
    }

    @Test
    @DisplayName("actualizarEstado debe cambiar el estado del reporte")
    void actualizarEstado_debeCambiarEstado() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reportePrueba));
        when(reporteRepository.save(any())).thenReturn(reportePrueba);

        reporteController.actualizarEstadoReporte(1L, new EstadoReporteDTO("CONTROLADO"));

        assertThat(reportePrueba.getEstado()).isEqualTo("CONTROLADO");
    }

    @Test
    @DisplayName("actualizarEstado debe guardar el reporte con el nuevo estado")
    void actualizarEstado_debeGuardarReporte() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reportePrueba));

        reporteController.actualizarEstadoReporte(1L, new EstadoReporteDTO("EXTINGUIDO"));

        verify(reporteRepository, times(1)).save(reportePrueba);
    }

    // ── eliminarReporte ───────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarReporte con ID existente debe retornar 204")
    void eliminarReporte_conIdExistente_debeRetornar204() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reportePrueba));
        doNothing().when(reporteRepository).deleteById(1L);

        var response = reporteController.eliminarReporte(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("eliminarReporte con ID inexistente debe retornar 404")
    void eliminarReporte_conIdInexistente_debeRetornar404() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        var response = reporteController.eliminarReporte(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(reporteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("eliminarReporte debe llamar deleteById exactamente una vez")
    void eliminarReporte_debeLlamarDeleteById() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reportePrueba));
        doNothing().when(reporteRepository).deleteById(1L);

        reporteController.eliminarReporte(1L);

        verify(reporteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarReporte con ID inexistente no debe llamar deleteById")
    void eliminarReporte_conIdInexistente_noDebeEliminar() {
        when(reporteRepository.findById(anyLong())).thenReturn(Optional.empty());

        reporteController.eliminarReporte(99L);

        verify(reporteRepository, never()).deleteById(anyLong());
    }
}
