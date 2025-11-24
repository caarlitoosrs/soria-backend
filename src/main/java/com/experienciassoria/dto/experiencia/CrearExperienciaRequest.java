package com.experienciassoria.dto.experiencia;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CrearExperienciaRequest {
    private String titulo;
    private String descripcion;
    private String categoria;
    private String imagenPortadaUrl;
    private String direccion;
    private BigDecimal ubicacionLat;
    private BigDecimal ubicacionLng;
}
