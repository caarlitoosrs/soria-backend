package com.experienciassoria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "experiencias_uid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienciaUID {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiencia_id", nullable = false)
    private Experiencia experiencia;

    @Column(unique = true, nullable = false)
    private String uid;

    private boolean activo = true;

    @Column(name = "fecha_generacion")
    private Instant fechaGeneracion = Instant.now();
}
