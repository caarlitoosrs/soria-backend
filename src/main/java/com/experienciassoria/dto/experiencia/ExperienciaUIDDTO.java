package com.experienciassoria.dto.experiencia;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExperienciaUIDDTO {
    private UUID id;
    private String uid;
    private boolean activo;
    private Instant fechaCreacion;
}

