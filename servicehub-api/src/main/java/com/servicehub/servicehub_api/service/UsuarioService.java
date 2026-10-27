package com.servicehub.servicehub_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.servicehub.servicehub_api.dto.UsuarioCadastroRequest;
import com.servicehub.servicehub_api.model.Role;
import com.servicehub.servicehub_api.model.Usuario;
import com.servicehub.servicehub_api.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository repository; private final PasswordEncoder encoder;
    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) { this.repository = repository; this.encoder = encoder; }
    public Usuario criar(UsuarioCadastroRequest request, boolean admin) {
        if (repository.existsByEmailIgnoreCase(request.email())) throw new IllegalArgumentException("E-mail já cadastrado");
        Usuario u = new Usuario(); u.setNome(request.nome()); u.setEmail(request.email());
        u.setSenha(encoder.encode(request.senha())); u.setRole(admin && request.role() != null ? request.role() : Role.CLIENT);
        return repository.save(u);
    }
}
