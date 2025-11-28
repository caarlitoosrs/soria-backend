package com.experienciassoria.service;

import com.experienciassoria.dto.comentario.*;
import com.experienciassoria.exception.ResourceNotFoundException;
import com.experienciassoria.model.*;
import com.experienciassoria.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ExperienciaRepository experienciaRepository;
    private final UsuarioRepository usuarioRepository;

    public ComentarioService(
            ComentarioRepository comentarioRepository,
            ExperienciaRepository experienciaRepository,
            UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.experienciaRepository = experienciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // 🔹 Obtener comentarios de una experiencia
    public List<ComentarioDTO> getComentariosByExperiencia(UUID experienciaId) {
        Experiencia experiencia = experienciaRepository.findById(experienciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        return comentarioRepository.findByExperienciaOrderByFechaDesc(experiencia).stream()
                .map(c -> new ComentarioDTO(
                        c.getId(),
                        c.getUsuario().getNombre(),
                        c.getTexto(),
                        c.getFecha()))
                .collect(Collectors.toList());
    }

    // 🔹 Crear un nuevo comentario
    public ComentarioDTO crearComentario(UUID usuarioId, UUID experienciaId, CrearComentarioRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Experiencia experiencia = experienciaRepository.findById(experienciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        Comentario comentario = Comentario.builder()
                .usuario(usuario)
                .experiencia(experiencia)
                .texto(request.getTexto())
                .fecha(Instant.now())
                .build();

        comentarioRepository.save(comentario);

        return new ComentarioDTO(
                comentario.getId(),
                usuario.getNombre(),
                comentario.getTexto(),
                comentario.getFecha());
    }

    public UUID getUsuarioIdByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
                .getId();
    }

}
