package com.servicehub.servicehub_api.dto;

import com.servicehub.servicehub_api.model.Role;
import com.servicehub.servicehub_api.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email, Role role) {
    public static UsuarioResponse from(Usuario u) { return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getRole()); }
}
