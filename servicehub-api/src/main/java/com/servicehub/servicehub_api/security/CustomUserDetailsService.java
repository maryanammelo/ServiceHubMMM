package com.servicehub.servicehub_api.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.servicehub.servicehub_api.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository repository;
    public CustomUserDetailsService(UsuarioRepository repository) { this.repository = repository; }
    @Override public UserDetails loadUserByUsername(String email) {
        var u = repository.findByEmailIgnoreCase(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return User.withUsername(u.getEmail()).password(u.getSenha()).roles(u.getRole().name()).build();
    }
}
