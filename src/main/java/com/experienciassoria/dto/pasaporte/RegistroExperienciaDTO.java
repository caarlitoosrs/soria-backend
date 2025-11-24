package com.experienciassoria.dto.pasaporte;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegistroExperienciaDTO {
    private UUID experienciaId;
    private String titulo;
    private String categoria;
    private Instant fechaRegistro;
    private String opinion;
    private String imgPortada;
    private int puntosOtorgados;
}
