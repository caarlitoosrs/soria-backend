package com.experienciassoria.dto.experiencia;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CrearExperienciaRequest {
    @NotBlank(message = "El título es requerido")
    private String titulo;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    @NotBlank(message = "La categoría es requerida")
    private String categoria;

    private String imagenPortadaUrl;

    private List<String> galeriaImagenes;

    private String direccion;

    private BigDecimal ubicacionLat;

    private BigDecimal ubicacionLng;

    @NotNull(message = "Los puntos otorgados son requeridos")
    @Min(value = 1, message = "Los puntos otorgados deben ser al menos 1")
    private Integer puntosOtorgados;
}
