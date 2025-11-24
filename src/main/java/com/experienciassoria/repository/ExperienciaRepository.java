package com.experienciassoria.repository;

import com.experienciassoria.model.Experiencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExperienciaRepository extends JpaRepository<Experiencia, UUID> {
}
