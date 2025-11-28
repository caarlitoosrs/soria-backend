package com.experienciassoria.service;

import com.experienciassoria.dto.admin.*;
import com.experienciassoria.dto.comentario.ComentarioDTO;
import com.experienciassoria.dto.pasaporte.PasaporteDTO;
import com.experienciassoria.dto.pasaporte.RegistroExperienciaDTO;
import com.experienciassoria.exception.ResourceNotFoundException;
import com.experienciassoria.exception.ValidationException;
import com.experienciassoria.model.Usuario;
import com.experienciassoria.repository.ComentarioRepository;
import com.experienciassoria.repository.RegistroExperienciaRepository;
import com.experienciassoria.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RegistroExperienciaRepository registroRepository;
    private final ComentarioRepository comentarioRepository;
    private final PasaporteService pasaporteService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RegistroExperienciaRepository registroRepository,
            ComentarioRepository comentarioRepository,
            PasaporteService pasaporteService) {
        this.usuarioRepository = usuarioRepository;
        this.registroRepository = registroRepository;
        this.comentarioRepository = comentarioRepository;
        this.pasaporteService = pasaporteService;
    }

    // 🔹 Listar todos los usuarios
    public List<UsuarioAdminDTO> getAllUsuarios() {
        log.info("Obteniendo lista de todos los usuarios");
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioAdminDTO(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getRole().name(),
                        u.getPuntos(),
                        u.getFechaCreacion(),
                        u.isActivo()))
                .collect(Collectors.toList());
    }

    // 🔹 Obtener detalles de un usuario
    public UsuarioDetailDTO getUsuarioById(UUID id) {
        log.info("Obteniendo detalles del usuario: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        long totalExperiencias = registroRepository.findByUsuario(usuario).size();
        long totalComentarios = comentarioRepository.findByUsuarioOrderByFechaDesc(usuario).size();

        return new UsuarioDetailDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getPuntos(),
                usuario.getFechaCreacion(),
                usuario.isActivo(),
                totalExperiencias,
                totalComentarios);
    }

    // 🔹 Actualizar usuario (rol principalmente)
    @Transactional
    public UsuarioDetailDTO actualizarUsuario(UUID id, ActualizarUsuarioRequest request) {
        log.info("Actualizando usuario {} con datos: {}", id, request);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.getRole() != null) {
            try {
                Usuario.Rol nuevoRol = Usuario.Rol.valueOf(request.getRole());
                usuario.setRole(nuevoRol);
                log.info("Rol actualizado a: {}", nuevoRol);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Rol inválido. Debe ser USER o ADMIN");
            }
        }

        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
            log.info("Estado activo actualizado a: {}", request.getActivo());
        }

        usuarioRepository.save(usuario);

        // Recalcular estadísticas
        long totalExperiencias = registroRepository.findByUsuario(usuario).size();
        long totalComentarios = comentarioRepository.findByUsuarioOrderByFechaDesc(usuario).size();

        return new UsuarioDetailDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getPuntos(),
                usuario.getFechaCreacion(),
                usuario.isActivo(),
                totalExperiencias,
                totalComentarios);
    }

    // 🔹 Eliminar usuario (soft delete)
    @Transactional
    public void eliminarUsuario(UUID id) {
        log.info("Eliminando usuario: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar que no sea el último ADMIN
        if (usuario.getRole() == Usuario.Rol.ADMIN) {
            long totalAdmins = usuarioRepository.findAll().stream()
                    .filter(u -> u.getRole() == Usuario.Rol.ADMIN && u.isActivo())
                    .count();
            
            if (totalAdmins <= 1) {
                throw new ValidationException("No se puede eliminar el último administrador del sistema");
            }
        }

        // Soft delete: marcar como inactivo
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario {} marcado como inactivo", id);
    }

    // 🔹 Obtener pasaporte de un usuario específico
    public PasaporteDTO getPasaporteUsuario(UUID id) {
        log.info("Obteniendo pasaporte del usuario: {}", id);
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        return pasaporteService.getPasaporte(id);
    }

    // 🔹 Obtener experiencias registradas por el usuario
    public List<RegistroExperienciaDTO> getExperienciasUsuario(UUID id) {
        log.info("Obteniendo experiencias del usuario: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return registroRepository.findByUsuario(usuario).stream()
                .map(r -> new RegistroExperienciaDTO(
                        r.getExperiencia().getId(),
                        r.getExperiencia().getTitulo(),
                        r.getExperiencia().getCategoria().name(),
                        r.getFechaRegistro(),
                        r.getOpinion(),
                        r.getImgPortada(),
                        r.getPuntosOtorgados()))
                .collect(Collectors.toList());
    }

    // 🔹 Obtener comentarios del usuario
    public List<ComentarioDTO> getComentariosUsuario(UUID id) {
        log.info("Obteniendo comentarios del usuario: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return comentarioRepository.findByUsuarioOrderByFechaDesc(usuario).stream()
                .map(c -> new ComentarioDTO(
                        c.getId(),
                        c.getUsuario().getNombre(),
                        c.getTexto(),
                        c.getFecha()))
                .collect(Collectors.toList());
    }

    // 🔹 Hacer admin a un usuario por email (público, sin JWT)
    @Transactional
    public UsuarioDetailDTO makeAdminByEmail(String email) {
        log.info("Haciendo admin al usuario con email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        usuario.setRole(Usuario.Rol.ADMIN);
        usuarioRepository.save(usuario);
        log.info("Usuario {} (email: {}) ahora es ADMIN", usuario.getId(), email);

        // Recalcular estadísticas
        long totalExperiencias = registroRepository.findByUsuario(usuario).size();
        long totalComentarios = comentarioRepository.findByUsuarioOrderByFechaDesc(usuario).size();

        return new UsuarioDetailDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getPuntos(),
                usuario.getFechaCreacion(),
                usuario.isActivo(),
                totalExperiencias,
                totalComentarios);
    }
}


