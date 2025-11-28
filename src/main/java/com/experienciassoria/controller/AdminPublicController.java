package com.experienciassoria.controller;

import com.experienciassoria.dto.admin.UsuarioDetailDTO;
import com.experienciassoria.exception.ValidationException;
import com.experienciassoria.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/admin")
public class AdminPublicController {

    private final UsuarioService usuarioService;

    public AdminPublicController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 🔹 POST /api/public/admin/make-admin/{id} — hacer admin a un usuario (público, sin JWT)
    @PostMapping("/make-admin/{id}")
    public ResponseEntity<UsuarioDetailDTO> makeAdmin(@PathVariable String id) {
        UUID userId = parseUUID(id);
        return ResponseEntity.ok(usuarioService.makeAdmin(userId));
    }

    private UUID parseUUID(String id) {
        try {
            // Si viene con prefijo 0x, removerlo
            String cleanId = id.startsWith("0x") || id.startsWith("0X") 
                ? id.substring(2) 
                : id;
            
            // Si no tiene guiones, asumir que es un UUID sin formato estándar
            if (!cleanId.contains("-")) {
                // Convertir de formato sin guiones a formato UUID estándar
                if (cleanId.length() == 32) {
                    cleanId = String.format("%s-%s-%s-%s-%s",
                        cleanId.substring(0, 8),
                        cleanId.substring(8, 12),
                        cleanId.substring(12, 16),
                        cleanId.substring(16, 20),
                        cleanId.substring(20, 32));
                }
            }
            
            return UUID.fromString(cleanId);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("ID de usuario inválido: " + id);
        }
    }
}

