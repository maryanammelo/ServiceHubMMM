package com.servicehub.servicehub_api.controller;

import java.util.List;
import com.servicehub.servicehub_api.dto.ServicoResponse;
import com.servicehub.servicehub_api.service.ServicoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ServicoGraphqlController {
    private final ServicoService service;
    public ServicoGraphqlController(ServicoService service) { this.service = service; }

    @QueryMapping
    public List<ServicoResponse> servicos() {
        return service.listarTodos().stream().map(ServicoResponse::from).toList();
    }

    @QueryMapping
    public ServicoResponse servico(@Argument Long id) {
        return ServicoResponse.from(service.buscarPorId(id));
    }
}
