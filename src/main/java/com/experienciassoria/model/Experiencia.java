package com.experienciassoria.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "experiencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Column(name = "imagen_portada_url")
    private String imagenPortadaUrl;

    @ElementCollection
    @CollectionTable(name = "experiencia_galeria_imagenes", joinColumns = @JoinColumn(name = "experiencia_id"))
    @Column(name = "imagen_url")
    private List<String> galeriaImagenes = new ArrayList<>();

    @Column(name = "ubicacion_lat")
    private BigDecimal ubicacionLat;

    @Column(name = "ubicacion_lng")
    private BigDecimal ubicacionLng;

    private String direccion;

    @Column(name = "puntos_otorgados")
    private Integer puntosOtorgados = 10;

    private boolean visible = true;

    @OneToMany(mappedBy = "experiencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExperienciaUID> uids = new ArrayList<>();

    @OneToMany(mappedBy = "experiencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroExperiencia> registros = new ArrayList<>();

    @OneToMany(mappedBy = "experiencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    public enum Categoria {
        RESTAURANTE, AIRE_LIBRE, MUSEO, MONUMENTO
    }
}
