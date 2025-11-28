package com.experienciassoria.dto.admin;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarUsuarioRequest {
    @Pattern(regexp = "USER|ADMIN", message = "El rol debe ser USER o ADMIN")
    private String role;
    
    private Boolean activo;
}

