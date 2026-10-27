package com.servicehub.servicehub_api.controller;

import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.servicehub.servicehub_api.dto.*;
import com.servicehub.servicehub_api.model.Usuario;
import com.servicehub.servicehub_api.security.JwtService;
import com.servicehub.servicehub_api.service.UsuarioService;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager manager; private final JwtService jwt; private final UsuarioService users;
    public AuthController(AuthenticationManager manager, JwtService jwt, UsuarioService users) { this.manager=manager; this.jwt=jwt; this.users=users; }
    @PostMapping("/login") public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication a = manager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        var role = a.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return new LoginResponse(jwt.gerar(a.getName(), role), "Bearer", jwt.getExpirationSeconds());
    }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse register(@Valid @RequestBody UsuarioCadastroRequest request) {
        return UsuarioResponse.from(users.criar(request, false));
    }
}
