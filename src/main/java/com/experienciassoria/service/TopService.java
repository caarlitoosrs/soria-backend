package com.experienciassoria.service;

import com.experienciassoria.dto.top.UsuarioRankingDTO;
import com.experienciassoria.model.Usuario;
import com.experienciassoria.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopService {

    private final UsuarioRepository usuarioRepository;

    public TopService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // 🔹 Obtener el top 10 de usuarios por puntos
    public List<UsuarioRankingDTO> getTopUsuarios() {
        return usuarioRepository.findTop10ByOrderByPuntosDesc().stream()
                .map(u -> new UsuarioRankingDTO(
                        u.getNombre(),
                        u.getPuntos(),
                        u.getRole().name()
                ))
                .collect(Collectors.toList());
    }
}
