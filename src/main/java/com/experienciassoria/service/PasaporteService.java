package com.experienciassoria.service;

import com.experienciassoria.dto.pasaporte.*;
import com.experienciassoria.exception.DuplicateResourceException;
import com.experienciassoria.exception.ResourceNotFoundException;
import com.experienciassoria.model.*;
import com.experienciassoria.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PasaporteService {

    private final RegistroExperienciaRepository registroRepo;
    private final ExperienciaUIDRepository experienciaUIDRepo;
    private final UsuarioRepository usuarioRepo;
    private final ExperienciaRepository experienciaRepo;
    private final EntityManager entityManager;

    public PasaporteService(
            RegistroExperienciaRepository registroRepo,
            ExperienciaUIDRepository experienciaUIDRepo,
            UsuarioRepository usuarioRepo,
            ExperienciaRepository experienciaRepo,
            EntityManager entityManager) {
        this.registroRepo = registroRepo;
        this.experienciaUIDRepo = experienciaUIDRepo;
        this.usuarioRepo = usuarioRepo;
        this.experienciaRepo = experienciaRepo;
        this.entityManager = entityManager;
    }

    public UUID getUsuarioIdByEmail(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
                .getId();
    }

    // 🔹 Obtener pasaporte completo de un usuario
    public PasaporteDTO getPasaporte(UUID usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

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
        log.info("Registrando experiencia para usuario {} con UID: {}", usuarioId, request.getUidScaneado());
        
        // 🔒 Bloqueo pesimista del usuario para prevenir condiciones de carrera
        // Esto serializa las operaciones de registro para el mismo usuario
        Usuario usuario = entityManager.find(Usuario.class, usuarioId, LockModeType.PESSIMISTIC_WRITE);
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        ExperienciaUID experienciaUID = experienciaUIDRepo.findByUidAndActivoTrue(request.getUidScaneado())
                .orElseThrow(() -> new ResourceNotFoundException("UID inválido o no activo"));

        Experiencia experiencia = experienciaUID.getExperiencia();

        // Verificar si ya existe el registro
        if (registroRepo.existsByUsuario_IdAndExperiencia_Id(usuarioId, experiencia.getId())) {
            throw new DuplicateResourceException("La experiencia ya fue registrada por este usuario");
        }

        int puntosOtorgados = experiencia.getPuntosOtorgados() != null ? experiencia.getPuntosOtorgados() : 10;

        RegistroExperiencia registro = RegistroExperiencia.builder()
                .usuario(usuario)
                .experiencia(experiencia)
                .experienciaUID(experienciaUID)
                .opinion(request.getOpinion())
                .imgPortada(experiencia.getImagenPortadaUrl())
                .fechaRegistro(Instant.now())
                .puntosOtorgados(puntosOtorgados)
                .build();

        try {
            registroRepo.save(registro);

            // 🔸 sumar puntos al usuario
            usuario.setPuntos(usuario.getPuntos() + puntosOtorgados);
            usuarioRepo.save(usuario);
            log.info("Experiencia registrada exitosamente. Usuario {} ahora tiene {} puntos", usuarioId, usuario.getPuntos());

            return new RegistroExperienciaDTO(
                    experiencia.getId(),
                    experiencia.getTitulo(),
                    experiencia.getCategoria().name(),
                    registro.getFechaRegistro(),
                    registro.getOpinion(),
                    registro.getImgPortada(),
                    registro.getPuntosOtorgados());
        } catch (DataIntegrityViolationException e) {
            // Capturar violación de constraint único (usuario_id + experiencia_id)
            log.warn("Intento de registro duplicado detectado por constraint de BD: usuario {} - experiencia {}", 
                    usuarioId, experiencia.getId());
            throw new DuplicateResourceException("La experiencia ya fue registrada por este usuario");
        }
    }
}
