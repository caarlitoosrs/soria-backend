package com.experienciassoria.controller;

import com.experienciassoria.dto.auth.*;
import com.experienciassoria.security.JwtUtils;
import com.experienciassoria.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> getMe(HttpServletRequest request) {
        String token = jwtUtils.extractTokenFromRequest(request);
        String email = jwtUtils.getEmailFromToken(token);
        return ResponseEntity.ok(authService.getMe(email));
    }

    @PutMapping("/me/foto-perfil")
    public ResponseEntity<UsuarioDto> actualizarFotoPerfil(
            @Valid @RequestBody ActualizarFotoPerfilRequest request,
            HttpServletRequest httpRequest) {
        String token = jwtUtils.extractTokenFromRequest(httpRequest);
        String email = jwtUtils.getEmailFromToken(token);
        return ResponseEntity.ok(authService.actualizarFotoPerfil(email, request.getFotoPerfilUrl()));
    }
}
