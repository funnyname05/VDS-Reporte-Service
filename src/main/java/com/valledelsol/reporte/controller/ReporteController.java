package com.valledelsol.reporte.controller;

import com.valledelsol.reporte.domain.CrearReporteDTO;
import com.valledelsol.reporte.domain.EstadoReporteDTO;
import com.valledelsol.reporte.domain.ReporteIncendio;
import com.valledelsol.reporte.domain.ReporteIncendioRepository;
import com.valledelsol.reporte.domain.ReporteResponseDTO;
import com.auth0.jwt.JWT;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reportes")  // ← sin /api, el BFF ya agrega ese prefijo
public class ReporteController {

    @Autowired
    private ReporteIncendioRepository reporteRepository;

    @PostMapping
    public ResponseEntity<ReporteResponseDTO> crearReporte(
            @RequestBody @Valid CrearReporteDTO datosReporte,
            @RequestHeader("Authorization") String authHeader  // ← leemos el JWT directo
    ) {
        // Extraemos el usuarioId del claim del token — sin tocar User
        var token = authHeader.replace("Bearer ", "");
        var decoded = JWT.decode(token);
        Long usuarioId = decoded.getClaim("id").asLong();

        // ReporteIncendio ahora recibe Long en vez de User
        ReporteIncendio nuevoReporte = new ReporteIncendio(datosReporte, usuarioId);
        reporteRepository.save(nuevoReporte);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ReporteResponseDTO(nuevoReporte));
    }

    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> listarReportes() {
        List<ReporteIncendio> reportes = reporteRepository.findAllByOrderByFechaDescHoraDesc();
        List<ReporteResponseDTO> response = reportes.stream()
                .map(ReporteResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    //@PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public ResponseEntity<ReporteResponseDTO> actualizarEstadoReporte(
            @PathVariable Long id,
            @RequestBody @Valid EstadoReporteDTO datos) {

        Optional<ReporteIncendio> reporteOptional = reporteRepository.findById(id);
        if (reporteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ReporteIncendio reporte = reporteOptional.get();
        reporte.actualizarEstado(datos.estado());
        reporteRepository.save(reporte);

        return ResponseEntity.ok(new ReporteResponseDTO(reporte));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        Optional<ReporteIncendio> reporteOptional = reporteRepository.findById(id);
        if (reporteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reporteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> obtenerReporte(@PathVariable Long id) {
        Optional<ReporteIncendio> reporte = reporteRepository.findById(id);
        if (reporte.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ReporteResponseDTO(reporte.get()));
    }
}