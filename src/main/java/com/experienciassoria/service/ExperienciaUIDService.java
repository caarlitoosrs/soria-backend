package com.experienciassoria.service;

import com.experienciassoria.dto.experiencia.GenerarUIDsResponse;
import com.experienciassoria.exception.ResourceNotFoundException;
import com.experienciassoria.exception.ValidationException;
import com.experienciassoria.model.Experiencia;
import com.experienciassoria.model.ExperienciaUID;
import com.experienciassoria.repository.ExperienciaRepository;
import com.experienciassoria.repository.ExperienciaUIDRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ExperienciaUIDService {

    private final ExperienciaUIDRepository experienciaUIDRepository;
    private final ExperienciaRepository experienciaRepository;

    public ExperienciaUIDService(ExperienciaUIDRepository experienciaUIDRepository,
                                  ExperienciaRepository experienciaRepository) {
        this.experienciaUIDRepository = experienciaUIDRepository;
        this.experienciaRepository = experienciaRepository;
    }

    @Transactional
    public GenerarUIDsResponse generarUids(UUID experienciaId, int cantidad) {
        log.info("Generando {} UIDs para experiencia {}", cantidad, experienciaId);
        Experiencia experiencia = experienciaRepository.findById(experienciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        if (cantidad < 1 || cantidad > 100) {
            throw new IllegalArgumentException("La cantidad debe estar entre 1 y 100");
        }

        List<String> uidsGenerados = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            String uid = null;
            boolean existe;
            int intentos = 0;
            final int MAX_INTENTOS = 10;

            // Generar UID único
            do {
                String uidGenerado = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
                final String uidParaComparar = uidGenerado;
                existe = experienciaUIDRepository.findByUidAndActivoTrue(uidGenerado).isPresent() ||
                         experienciaUIDRepository.findAll().stream()
                                 .anyMatch(e -> e.getUid().equals(uidParaComparar));
                if (!existe) {
                    uid = uidGenerado;
                }
                intentos++;
            } while (existe && intentos < MAX_INTENTOS);

            if (uid == null) {
                throw new ValidationException("No se pudo generar un UID único después de " + MAX_INTENTOS + " intentos");
            }

            final String uidFinal = uid;
            ExperienciaUID experienciaUID = ExperienciaUID.builder()
                    .experiencia(experiencia)
                    .uid(uidFinal)
                    .activo(true)
                    .fechaGeneracion(Instant.now())
                    .build();

            experienciaUIDRepository.save(experienciaUID);
            uidsGenerados.add(uidFinal);
        }

        log.info("UIDs generados exitosamente para experiencia {}: {} UIDs", experienciaId, cantidad);
        return new GenerarUIDsResponse(experienciaId, cantidad, uidsGenerados);
    }
}

