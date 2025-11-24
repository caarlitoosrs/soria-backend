package com.experienciassoria.repository;

import com.experienciassoria.model.RegistroExperiencia;
import com.experienciassoria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistroExperienciaRepository extends JpaRepository<RegistroExperiencia, UUID> {

    // 🔹 Buscar todas las experiencias registradas por un usuario
    List<RegistroExperiencia> findByUsuario(Usuario usuario);

    // 🔹 Comprobar si el usuario ya registró una experiencia concreta
    boolean existsByUsuario_IdAndExperiencia_Id(UUID usuarioId, UUID experienciaId);

    // 🔹 Buscar un registro concreto
    Optional<RegistroExperiencia> findByUsuario_IdAndExperiencia_Id(UUID usuarioId, UUID experienciaId);
}
