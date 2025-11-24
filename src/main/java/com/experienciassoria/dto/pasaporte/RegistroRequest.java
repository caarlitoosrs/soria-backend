package com.experienciassoria.dto.pasaporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {
    private String uidScaneado;  // UID escaneado del QR
    private String opinion;      // Opinión opcional del usuario
}
