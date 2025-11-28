package com.experienciassoria.controller;

import com.experienciassoria.dto.admin.UsuarioDetailDTO;
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
    public ResponseEntity<UsuarioDetailDTO> makeAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.makeAdmin(id));
    }
}

