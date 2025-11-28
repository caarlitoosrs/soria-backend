package com.experienciassoria.controller;

import com.experienciassoria.dto.admin.UsuarioDetailDTO;
import com.experienciassoria.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/admin")
public class AdminPublicController {

    private final UsuarioService usuarioService;

    public AdminPublicController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 🔹 POST /api/public/admin/make-admin/{email} — hacer admin a un usuario por email (público, sin JWT)
    @PostMapping("/make-admin/{email}")
    public ResponseEntity<UsuarioDetailDTO> makeAdmin(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.makeAdminByEmail(email));
    }
}

