package com.experienciassoria.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearComentarioRequest {
    @NotBlank(message = "El texto del comentario es requerido")
    @Size(min = 1, max = 500, message = "El comentario debe tener entre 1 y 500 caracteres")
    private String texto;
}
