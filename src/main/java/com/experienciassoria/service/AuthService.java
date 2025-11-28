package com.experienciassoria.service;

import com.experienciassoria.dto.auth.*;
import com.experienciassoria.exception.DuplicateResourceException;
import com.experienciassoria.exception.ResourceNotFoundException;
import com.experienciassoria.exception.ValidationException;
import com.experienciassoria.model.Usuario;
import com.experienciassoria.repository.UsuarioRepository;
import com.experienciassoria.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Registrando nuevo usuario con email: {}", request.getEmail());
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de registro con email duplicado: {}", request.getEmail());
            throw new DuplicateResourceException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Usuario.Rol.USER)
                .fechaCreacion(Instant.now())
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente: {}", request.getEmail());

        String token = jwtUtils.generateToken(usuario.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login con email: {}", request.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new ValidationException("Contraseña incorrecta");
        }

        String token = jwtUtils.generateToken(usuario.getEmail());
        return new AuthResponse(token);
    }

    public UsuarioDto getMe(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return new UsuarioDto(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRole().name(), usuario.getPuntos(), usuario.getFotoPerfilUrl());
    }

    @Transactional
    public UsuarioDto actualizarFotoPerfil(String email, String fotoPerfilUrl) {
        log.info("Actualizando foto de perfil para usuario: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setFotoPerfilUrl(fotoPerfilUrl);
        usuarioRepository.save(usuario);
        log.info("Foto de perfil actualizada exitosamente para usuario: {}", email);

        return new UsuarioDto(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRole().name(), usuario.getPuntos(), usuario.getFotoPerfilUrl());
    }
}
