package com.experienciassoria.repository;

import com.experienciassoria.model.ExperienciaUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExperienciaUIDRepository extends JpaRepository<ExperienciaUID, UUID> {

    // 🔹 Buscar un UID activo (para validar escaneo)
    Optional<ExperienciaUID> findByUidAndActivoTrue(String uid);
}
