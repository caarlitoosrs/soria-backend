package com.experienciassoria.service;

import com.experienciassoria.dto.pasaporte.*;
import com.experienciassoria.model.*;
import com.experienciassoria.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PasaporteService {

    private final RegistroExperienciaRepository registroRepo;
    private final ExperienciaUIDRepository experienciaUIDRepo;
    private final UsuarioRepository usuarioRepo;
    private final ExperienciaRepository experienciaRepo;

    public PasaporteService(
            RegistroExperienciaRepository registroRepo,
            ExperienciaUIDRepository experienciaUIDRepo,
            UsuarioRepository usuarioRepo,
            ExperienciaRepository experienciaRepo) {
        this.registroRepo = registroRepo;
        this.experienciaUIDRepo = experienciaUIDRepo;
        this.usuarioRepo = usuarioRepo;
        this.experienciaRepo = experienciaRepo;
    }

    public UUID getUsuarioIdByEmail(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    // 🔹 Obtener pasaporte completo de un usuario
    public PasaporteDTO getPasaporte(UUID usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<RegistroExperienciaDTO> registros = registroRepo.findByUsuario(usuario).stream()
                .map(r -> new RegistroExperienciaDTO(
                        r.getExperiencia().getId(),
                        r.getExperiencia().getTitulo(),
                        r.getExperiencia().getCategoria().name(),
                        r.getFechaRegistro(),
                        r.getExperiencia().getImagenPortadaUrl(),
                        r.getOpinion(),
                        r.getPuntosOtorgados()))
                .collect(Collectors.toList());

        return new PasaporteDTO(usuario.getId(), usuario.getNombre(), usuario.getPuntos(), registros);
    }

    // 🔹 Registrar una experiencia (a partir de un UID)
    @Transactional
    public RegistroExperienciaDTO registrarExperiencia(UUID usuarioId, RegistroRequest request) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ExperienciaUID experienciaUID = experienciaUIDRepo.findByUidAndActivoTrue(request.getUidScaneado())
                .orElseThrow(() -> new RuntimeException("UID inválido o no activo"));

        Experiencia experiencia = experienciaUID.getExperiencia();

        if (registroRepo.existsByUsuario_IdAndExperiencia_Id(usuarioId, experiencia.getId())) {
            throw new RuntimeException("La experiencia ya fue registrada por este usuario");
        }

        RegistroExperiencia registro = RegistroExperiencia.builder()
                .usuario(usuario)
                .experiencia(experiencia)
                .experienciaUID(experienciaUID)
                .opinion(request.getOpinion())
                .imgPortada(experiencia.getImagenPortadaUrl())
                .fechaRegistro(Instant.now())
                .puntosOtorgados(10)
                .build();

        registroRepo.save(registro);

        // 🔸 sumar puntos al usuario
        usuario.setPuntos(usuario.getPuntos() + registro.getPuntosOtorgados());
        usuarioRepo.save(usuario);

        return new RegistroExperienciaDTO(
                experiencia.getId(),
                experiencia.getTitulo(),
                experiencia.getCategoria().name(),
                registro.getFechaRegistro(),
                registro.getOpinion(),
                registro.getImgPortada(),
                registro.getPuntosOtorgados());
    }
}
