package com.experienciassoria.repository;

import com.experienciassoria.model.Experiencia;
import com.experienciassoria.model.ExperienciaUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExperienciaUIDRepository extends JpaRepository<ExperienciaUID, UUID> {

    // 🔹 Buscar un UID activo (para validar escaneo)
    Optional<ExperienciaUID> findByUidAndActivoTrue(String uid);

    // 🔹 Buscar todos los UIDs de una experiencia
    List<ExperienciaUID> findByExperiencia(Experiencia experiencia);
}
