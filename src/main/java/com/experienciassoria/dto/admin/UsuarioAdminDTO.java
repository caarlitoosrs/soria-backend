package com.experienciassoria.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UsuarioAdminDTO {
    private UUID id;
    private String nombre;
    private String email;
    private String role;
    private int puntos;
    private Instant fechaCreacion;
    private boolean activo;
}


