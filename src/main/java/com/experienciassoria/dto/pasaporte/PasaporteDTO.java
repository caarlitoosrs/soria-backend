package com.experienciassoria.dto.pasaporte;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PasaporteDTO {
    private UUID usuarioId;
    private String nombreUsuario;
    private int puntosTotales;
    private List<RegistroExperienciaDTO> registros;
}
