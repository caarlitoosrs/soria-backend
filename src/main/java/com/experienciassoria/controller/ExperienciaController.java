package com.experienciassoria.controller;

import com.experienciassoria.dto.experiencia.*;
import com.experienciassoria.service.ExperienciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiencias")
public class ExperienciaController {

    private final ExperienciaService experienciaService;

    public ExperienciaController(ExperienciaService experienciaService) {
        this.experienciaService = experienciaService;
    }

    // 🔹 GET /api/experiencias — todas las experiencias
    @GetMapping
    public ResponseEntity<List<ExperienciaListDTO>> getAllExperiencias() {
        return ResponseEntity.ok(experienciaService.getAllExperiencias());
    }

    // 🔹 GET /api/experiencias/{id} — detalle
    @GetMapping("/{id}")
    public ResponseEntity<ExperienciaDetailDTO> getExperienciaById(@PathVariable UUID id) {
        return ResponseEntity.ok(experienciaService.getExperienciaById(id));
    }

    // 🔹 POST /api/experiencias — crear nueva experiencia (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ExperienciaDetailDTO> crearExperiencia(@RequestBody CrearExperienciaRequest request) {
        return ResponseEntity.ok(experienciaService.crearExperiencia(request));
    }

    // 🔹 POST /api/experiencias/{id}/generar-uid — placeholder, se hará luego
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/generar-uid")
    public ResponseEntity<String> generarUids(@PathVariable UUID id, @RequestParam(defaultValue = "1") int cantidad) {
        // TODO: implementar en ExperienciaUIDService más adelante
        return ResponseEntity.ok("Generar " + cantidad + " UIDs para experiencia " + id);
    }
}
