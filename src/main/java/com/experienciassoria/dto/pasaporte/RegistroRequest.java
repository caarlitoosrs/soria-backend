package com.experienciassoria.dto.pasaporte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {
    @NotBlank(message = "El UID escaneado es requerido")
    private String uidScaneado;  // UID escaneado del QR

    @Size(max = 1000, message = "La opinión no puede exceder 1000 caracteres")
    private String opinion;      // Opinión opcional del usuario
}
