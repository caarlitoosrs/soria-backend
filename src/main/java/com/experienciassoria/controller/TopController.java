package com.experienciassoria.controller;

import com.experienciassoria.dto.top.UsuarioRankingDTO;
import com.experienciassoria.service.TopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/top")
public class TopController {

    private final TopService topService;

    public TopController(TopService topService) {
        this.topService = topService;
    }

    // 🔹 GET /api/top — obtener top de usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioRankingDTO>> getTopUsuarios() {
        return ResponseEntity.ok(topService.getTopUsuarios());
    }
}
