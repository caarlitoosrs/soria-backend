package com.experienciassoria.controller;

import com.experienciassoria.dto.admin.*;
import com.experienciassoria.dto.comentario.ComentarioDTO;
import com.experienciassoria.dto.pasaporte.PasaporteDTO;
import com.experienciassoria.dto.pasaporte.RegistroExperienciaDTO;
import com.experienciassoria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 🔹 GET /api/admin/usuarios — listar todos los usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioAdminDTO>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    // 🔹 GET /api/admin/usuarios/{id} — detalles de un usuario
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDetailDTO> getUsuarioById(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    // 🔹 PUT /api/admin/usuarios/{id} — actualizar usuario (rol)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDetailDTO> actualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request));
    }

    // 🔹 DELETE /api/admin/usuarios/{id} — eliminar usuario
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 GET /api/admin/usuarios/{id}/pasaporte — ver pasaporte del usuario
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/pasaporte")
    public ResponseEntity<PasaporteDTO> getPasaporteUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.getPasaporteUsuario(id));
    }

    // 🔹 GET /api/admin/usuarios/{id}/experiencias — ver experiencias registradas
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/experiencias")
    public ResponseEntity<List<RegistroExperienciaDTO>> getExperienciasUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.getExperienciasUsuario(id));
    }

    // 🔹 GET /api/admin/usuarios/{id}/comentarios — ver comentarios del usuario
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<ComentarioDTO>> getComentariosUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.getComentariosUsuario(id));
    }
}


