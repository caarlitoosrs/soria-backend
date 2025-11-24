package com.experienciassoria.controller;

import com.experienciassoria.dto.comentario.*;
import com.experienciassoria.security.JwtUtils;
import com.experienciassoria.service.ComentarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiencias/{experienciaId}/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final JwtUtils jwtUtils;

    public ComentarioController(ComentarioService comentarioService, JwtUtils jwtUtils) {
        this.comentarioService = comentarioService;
        this.jwtUtils = jwtUtils;
    }

    // 🔹 GET /api/experiencias/{id}/comentarios — obtener comentarios
    @GetMapping
    public ResponseEntity<List<ComentarioDTO>> getComentarios(@PathVariable UUID experienciaId) {
        return ResponseEntity.ok(comentarioService.getComentariosByExperiencia(experienciaId));
    }

    // 🔹 POST /api/experiencias/{id}/comentarios — crear comentario (usuario autenticado)
    @PostMapping
    public ResponseEntity<ComentarioDTO> crearComentario(
            @PathVariable UUID experienciaId,
            @RequestBody CrearComentarioRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        String email = jwtUtils.getEmailFromToken(token);
        UUID usuarioId = comentarioService.getUsuarioIdByEmail(email); // lo añadimos como helper

        ComentarioDTO nuevo = comentarioService.crearComentario(usuarioId, experienciaId, request);
        return ResponseEntity.ok(nuevo);
    }
}
