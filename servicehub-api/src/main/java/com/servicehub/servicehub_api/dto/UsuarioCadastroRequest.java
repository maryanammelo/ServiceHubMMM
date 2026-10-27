package com.servicehub.servicehub_api.dto;

import com.servicehub.servicehub_api.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.util.Locale;

public record UsuarioCadastroRequest(
        @NotBlank @Size(max=120) @Pattern(regexp="[^<>\\p{Cntrl}]*") String nome,
        @NotBlank @Email @Size(max=180) String email,
        @NotBlank @Size(min = 8, max = 72) String senha,
        Role role) {
    public UsuarioCadastroRequest {
        nome = nome == null ? null : nome.strip();
        email = email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }
    @com.fasterxml.jackson.annotation.JsonIgnore
    @jakarta.validation.constraints.AssertTrue(message="A senha deve possuir no máximo 72 bytes UTF-8 (limite do BCrypt)")
    public boolean isSenhaDentroDoLimite() {
        return senha == null || senha.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72;
    }
}
