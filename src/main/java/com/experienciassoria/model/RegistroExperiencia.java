package com.experienciassoria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "registro_experiencia",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "experiencia_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroExperiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiencia_id", nullable = false)
    private Experiencia experiencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiencia_uid_id", nullable = false)
    private ExperienciaUID experienciaUID;

    @Column(name = "fecha_registro")
    private Instant fechaRegistro = Instant.now();

    @Column(columnDefinition = "TEXT")
    private String opinion;

    private String imgPortada;

    @Column(name = "puntos_otorgados")
    private int puntosOtorgados = 10;
}
