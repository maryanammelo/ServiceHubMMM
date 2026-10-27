package com.servicehub.servicehub_api.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Digits;

public record ServicoRequest(
        @NotBlank @Size(min=3,max=100) @Pattern(regexp="[^<>\\p{Cntrl}]*", message="Use texto simples, sem marcação HTML") String nome,
        @NotBlank @Size(max=500) @Pattern(regexp="[^<>\\p{Cntrl}]*", message="Use texto simples, sem marcação HTML") String descricao,
        @NotBlank @Size(max=80) @Pattern(regexp="[^<>\\p{Cntrl}]*", message="Use texto simples, sem marcação HTML") String categoria,
        @NotNull @DecimalMin("0.01") @Digits(integer=8, fraction=2) BigDecimal preco,
        @NotNull Boolean disponivel) {
    public ServicoRequest {
        nome = nome == null ? null : nome.strip();
        descricao = descricao == null ? null : descricao.strip();
        categoria = categoria == null ? null : categoria.strip();
    }
}
