package com.experienciassoria.controller;

import com.experienciassoria.dto.experiencia.*;
import com.experienciassoria.service.ExperienciaService;
import com.experienciassoria.service.ExperienciaUIDService;
import com.experienciassoria.service.QrCodeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiencias")
public class ExperienciaController {

    private final ExperienciaService experienciaService;
    private final ExperienciaUIDService experienciaUIDService;
    private final QrCodeService qrCodeService;

    public ExperienciaController(ExperienciaService experienciaService,
                                 ExperienciaUIDService experienciaUIDService,
                                 QrCodeService qrCodeService) {
        this.experienciaService = experienciaService;
        this.experienciaUIDService = experienciaUIDService;
        this.qrCodeService = qrCodeService;
    }

    // 🔹 GET /api/experiencias — todas las experiencias (visibles para público, todas para ADMIN)
    @GetMapping
    public ResponseEntity<List<ExperienciaListDTO>> getAllExperiencias() {
        // Si el usuario es ADMIN, devolver todas las experiencias (visibles y no visibles)
        // Si no es ADMIN, devolver solo las visibles
        // Por ahora, siempre devolvemos todas para el admin panel
        // TODO: Verificar rol del usuario autenticado
        return ResponseEntity.ok(experienciaService.getAllExperienciasAdmin());
    }

    // 🔹 GET /api/experiencias/{id} — detalle
    @GetMapping("/{id}")
    public ResponseEntity<ExperienciaDetailDTO> getExperienciaById(@PathVariable UUID id) {
        return ResponseEntity.ok(experienciaService.getExperienciaById(id));
    }

    // 🔹 GET /api/experiencias/uid/{uid} — obtener experiencia por UID (público)
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ExperienciaDetailDTO> getExperienciaByUid(@PathVariable String uid) {
        return ResponseEntity.ok(experienciaService.getExperienciaByUid(uid));
    }

    // 🔹 POST /api/experiencias — crear nueva experiencia (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ExperienciaDetailDTO> crearExperiencia(@Valid @RequestBody CrearExperienciaRequest request) {
        return ResponseEntity.ok(experienciaService.crearExperiencia(request));
    }

    // 🔹 PUT /api/experiencias/{id} — actualizar experiencia (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ExperienciaDetailDTO> actualizarExperiencia(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarExperienciaRequest request) {
        return ResponseEntity.ok(experienciaService.actualizarExperiencia(id, request));
    }

    // 🔹 DELETE /api/experiencias/{id} — eliminar experiencia (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarExperiencia(@PathVariable UUID id) {
        experienciaService.eliminarExperiencia(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 GET /api/experiencias/{id}/uids — obtener UIDs de una experiencia (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/uids")
    public ResponseEntity<List<ExperienciaUIDDTO>> getUidsByExperiencia(@PathVariable UUID id) {
        return ResponseEntity.ok(experienciaService.getUidsByExperiencia(id));
    }

    // 🔹 POST /api/experiencias/{id}/generar-uid — generar UIDs para una experiencia (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/generar-uid")
    public ResponseEntity<GenerarUIDsResponse> generarUids(@PathVariable UUID id, @RequestParam(defaultValue = "1") int cantidad) {
        return ResponseEntity.ok(experienciaUIDService.generarUids(id, cantidad));
    }

    // 🔹 GET /api/experiencias/uids/{uidId}/qr — generar QR code de un UID (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/uids/{uidId}/qr")
    public ResponseEntity<Map<String, String>> generarQrCode(@PathVariable UUID uidId) {
        String qrCodeBase64 = qrCodeService.generarQrCode(uidId);
        Map<String, String> response = new HashMap<>();
        response.put("qrCode", qrCodeBase64);
        return ResponseEntity.ok(response);
    }
}
