package com.servicehub.servicehub_api.dto;

import java.math.BigDecimal;
import com.servicehub.servicehub_api.model.Servico;

public record ServicoResponse(Long id, String nome, String descricao, String categoria, BigDecimal preco, Boolean disponivel) {
    public static ServicoResponse from(Servico s) { return new ServicoResponse(s.getId(),s.getNome(),s.getDescricao(),s.getCategoria(),s.getPreco(),s.getDisponivel()); }
}
