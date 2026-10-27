package com.servicehub.servicehub_api.controller;

import com.servicehub.servicehub_api.dto.UsuarioCadastroRequest;
import com.servicehub.servicehub_api.dto.UsuarioResponse;
import com.servicehub.servicehub_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {
    private final UsuarioService users;

    public UsuarioController(UsuarioService users) { this.users = users; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse criar(@Valid @RequestBody UsuarioCadastroRequest request) {
        return UsuarioResponse.from(users.criar(request, true));
    }
}
