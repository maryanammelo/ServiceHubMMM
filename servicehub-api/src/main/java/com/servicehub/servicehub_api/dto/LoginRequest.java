package com.servicehub.servicehub_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) {
    public LoginRequest {
        email = email == null ? null : email.strip().toLowerCase(java.util.Locale.ROOT);
    }
}
