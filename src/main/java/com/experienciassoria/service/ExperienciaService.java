package com.experienciassoria.service;

import com.experienciassoria.dto.experiencia.*;
import com.experienciassoria.exception.ResourceNotFoundException;
import com.experienciassoria.model.Experiencia;
import com.experienciassoria.repository.ExperienciaRepository;
import com.experienciassoria.repository.ExperienciaUIDRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExperienciaService {

    private final ExperienciaRepository experienciaRepository;
    private final ExperienciaUIDRepository experienciaUIDRepository;

    public ExperienciaService(ExperienciaRepository experienciaRepository,
                              ExperienciaUIDRepository experienciaUIDRepository) {
        this.experienciaRepository = experienciaRepository;
        this.experienciaUIDRepository = experienciaUIDRepository;
    }

    // 🔹 Obtener todas las experiencias (lista) - solo visibles para usuarios públicos
    public List<ExperienciaListDTO> getAllExperiencias() {
        return experienciaRepository.findAllByVisibleTrue().stream()
                .map(exp -> new ExperienciaListDTO(
                        exp.getId(),
                        exp.getTitulo(),
                        exp.getCategoria().name(),
                        exp.getImagenPortadaUrl(),
                        exp.getUbicacionLat(),
                        exp.getUbicacionLng(),
                        exp.isVisible()
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Obtener todas las experiencias (lista) - incluye visibles y no visibles (solo ADMIN)
    public List<ExperienciaListDTO> getAllExperienciasAdmin() {
        log.info("Obteniendo todas las experiencias (admin)");
        return experienciaRepository.findAll().stream()
                .map(exp -> new ExperienciaListDTO(
                        exp.getId(),
                        exp.getTitulo(),
                        exp.getCategoria().name(),
                        exp.getImagenPortadaUrl(),
                        exp.getUbicacionLat(),
                        exp.getUbicacionLng(),
                        exp.isVisible()
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Obtener detalles de una experiencia
    public ExperienciaDetailDTO getExperienciaById(UUID id) {
        Experiencia exp = experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        return new ExperienciaDetailDTO(
                exp.getId(),
                exp.getTitulo(),
                exp.getDescripcion(),
                exp.getCategoria().name(),
                exp.getImagenPortadaUrl(),
                exp.getGaleriaImagenes() != null ? exp.getGaleriaImagenes() : new ArrayList<>(),
                exp.getDireccion(),
                exp.getUbicacionLat(),
                exp.getUbicacionLng(),
                exp.getPuntosOtorgados() != null ? exp.getPuntosOtorgados() : 10,
                exp.isVisible()
        );
    }

    // 🔹 Crear nueva experiencia (solo ADMIN)
    public ExperienciaDetailDTO crearExperiencia(CrearExperienciaRequest request) {
        log.info("Creando nueva experiencia: {}", request.getTitulo());
        Experiencia experiencia = Experiencia.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .categoria(Experiencia.Categoria.valueOf(request.getCategoria()))
                .imagenPortadaUrl(request.getImagenPortadaUrl())
                .galeriaImagenes(request.getGaleriaImagenes() != null ? request.getGaleriaImagenes() : new ArrayList<>())
                .direccion(request.getDireccion())
                .ubicacionLat(request.getUbicacionLat())
                .ubicacionLng(request.getUbicacionLng())
                .puntosOtorgados(request.getPuntosOtorgados() != null && request.getPuntosOtorgados() > 0 ? request.getPuntosOtorgados() : 10)
                .visible(true)
                .build();

        experienciaRepository.save(experiencia);

        return getExperienciaById(experiencia.getId());
    }

    // 🔹 Actualizar experiencia (solo ADMIN)
    public ExperienciaDetailDTO actualizarExperiencia(UUID id, ActualizarExperienciaRequest request) {
        log.info("Actualizando experiencia con ID: {}", id);
        Experiencia experiencia = experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        if (request.getTitulo() != null) {
            experiencia.setTitulo(request.getTitulo());
        }
        if (request.getDescripcion() != null) {
            experiencia.setDescripcion(request.getDescripcion());
        }
        if (request.getCategoria() != null) {
            experiencia.setCategoria(Experiencia.Categoria.valueOf(request.getCategoria()));
        }
        if (request.getImagenPortadaUrl() != null) {
            experiencia.setImagenPortadaUrl(request.getImagenPortadaUrl());
        }
        if (request.getGaleriaImagenes() != null) {
            experiencia.setGaleriaImagenes(request.getGaleriaImagenes());
        }
        if (request.getDireccion() != null) {
            experiencia.setDireccion(request.getDireccion());
        }
        if (request.getUbicacionLat() != null) {
            experiencia.setUbicacionLat(request.getUbicacionLat());
        }
        if (request.getUbicacionLng() != null) {
            experiencia.setUbicacionLng(request.getUbicacionLng());
        }
        if (request.getVisible() != null) {
            experiencia.setVisible(request.getVisible());
        }
        if (request.getPuntosOtorgados() != null) {
            experiencia.setPuntosOtorgados(request.getPuntosOtorgados());
        }

        experienciaRepository.save(experiencia);

        return getExperienciaById(experiencia.getId());
    }

    // 🔹 Eliminar experiencia (solo ADMIN)
    public void eliminarExperiencia(UUID id) {
        log.info("Eliminando experiencia con ID: {}", id);
        Experiencia experiencia = experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));
        experienciaRepository.delete(experiencia);
        log.info("Experiencia eliminada exitosamente: {}", id);
    }

    // 🔹 Obtener UIDs de una experiencia (solo ADMIN)
    public List<ExperienciaUIDDTO> getUidsByExperiencia(UUID experienciaId) {
        Experiencia experiencia = experienciaRepository.findById(experienciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        return experienciaUIDRepository.findByExperiencia(experiencia).stream()
                .map(uid -> new ExperienciaUIDDTO(
                        uid.getId(),
                        uid.getUid(),
                        uid.isActivo(),
                        uid.getFechaGeneracion() // Mapea fechaGeneracion del modelo a fechaCreacion del DTO
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Obtener experiencia por UID (público)
    public ExperienciaDetailDTO getExperienciaByUid(String uid) {
        com.experienciassoria.model.ExperienciaUID experienciaUID = experienciaUIDRepository.findByUidAndActivoTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("UID no encontrado o no activo"));

        Experiencia experiencia = experienciaUID.getExperiencia();
        return getExperienciaById(experiencia.getId());
    }
}
