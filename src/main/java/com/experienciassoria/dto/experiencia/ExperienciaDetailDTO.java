package com.experienciassoria.dto.experiencia;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExperienciaDetailDTO {
    private UUID id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String imagenPortadaUrl;
    private List<String> galeriaImagenes;
    private String direccion;
    private BigDecimal ubicacionLat;
    private BigDecimal ubicacionLng;
    private Integer puntosOtorgados;
    private boolean visible;
}
