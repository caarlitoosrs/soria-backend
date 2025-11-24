package com.experienciassoria.dto.experiencia;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExperienciaListDTO {
    private UUID id;
    private String titulo;
    private String categoria;
    private String imagenPortadaUrl;
    private boolean visible;
}
