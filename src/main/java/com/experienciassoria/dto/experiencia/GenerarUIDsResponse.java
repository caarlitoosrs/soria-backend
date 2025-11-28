package com.experienciassoria.dto.experiencia;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GenerarUIDsResponse {
    private UUID experienciaId;
    private int cantidadGenerados;
    private List<String> uids;
}

