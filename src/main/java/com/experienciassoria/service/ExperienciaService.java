package com.experienciassoria.service;

import com.experienciassoria.dto.experiencia.*;
import com.experienciassoria.model.Experiencia;
import com.experienciassoria.repository.ExperienciaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExperienciaService {

    private final ExperienciaRepository experienciaRepository;

    public ExperienciaService(ExperienciaRepository experienciaRepository) {
        this.experienciaRepository = experienciaRepository;
    }

    // 🔹 Obtener todas las experiencias (lista)
    public List<ExperienciaListDTO> getAllExperiencias() {
        return experienciaRepository.findAll().stream()
                .map(exp -> new ExperienciaListDTO(
                        exp.getId(),
                        exp.getTitulo(),
                        exp.getCategoria().name(),
                        exp.getImagenPortadaUrl(),
                        exp.isVisible()
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Obtener detalles de una experiencia
    public ExperienciaDetailDTO getExperienciaById(UUID id) {
        Experiencia exp = experienciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experiencia no encontrada"));

        return new ExperienciaDetailDTO(
                exp.getId(),
                exp.getTitulo(),
                exp.getDescripcion(),
                exp.getCategoria().name(),
                exp.getImagenPortadaUrl(),
                exp.getDireccion(),
                exp.getUbicacionLat(),
                exp.getUbicacionLng(),
                exp.isVisible()
        );
    }

    // 🔹 Crear nueva experiencia (solo ADMIN)
    public ExperienciaDetailDTO crearExperiencia(CrearExperienciaRequest request) {
        Experiencia experiencia = Experiencia.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .categoria(Experiencia.Categoria.valueOf(request.getCategoria()))
                .imagenPortadaUrl(request.getImagenPortadaUrl())
                .direccion(request.getDireccion())
                .ubicacionLat(request.getUbicacionLat())
                .ubicacionLng(request.getUbicacionLng())
                .visible(true)
                .build();

        experienciaRepository.save(experiencia);

        return getExperienciaById(experiencia.getId());
    }
}
