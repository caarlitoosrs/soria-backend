package com.experienciassoria.dto.comentario;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ComentarioDTO {
    private UUID id;
    private String autorNombre;
    private String texto;
    private Instant fecha;
}
