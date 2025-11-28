package com.experienciassoria.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UsuarioDto {
    private UUID id;
    private String nombre;
    private String email;
    private String role;
    private int puntos;
    private String fotoPerfilUrl;
}
