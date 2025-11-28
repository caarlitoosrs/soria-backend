package com.experienciassoria.dto.experiencia;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ActualizarExperienciaRequest {
    private String titulo;
    private String descripcion;
    private String categoria;
    private String imagenPortadaUrl;
    private List<String> galeriaImagenes;
    private String direccion;
    private BigDecimal ubicacionLat;
    private BigDecimal ubicacionLng;
    private Boolean visible;
    private Integer puntosOtorgados;
}

