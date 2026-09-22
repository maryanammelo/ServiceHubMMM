package com.servicehub.servicehub_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.servicehub.servicehub_api.model.Servico;
import com.servicehub.servicehub_api.service.ServicoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servicos")
@Tag(
    name = "Serviços",
    description = "Endpoints para cadastro, consulta, atualização e exclusão de serviços"
)
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    @Operation(
        summary = "Cadastrar serviço",
        description = "Cadastra um novo serviço na plataforma"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Serviço cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos")
    })
    public ResponseEntity<Servico> criar(
            @Valid @RequestBody Servico servico) {

        Servico novoServico = servicoService.criar(servico);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novoServico);
    }

    @GetMapping
    @Operation(
        summary = "Listar serviços",
        description = "Retorna todos os serviços cadastrados"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de serviços retornada com sucesso"
    )
    public ResponseEntity<List<Servico>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar serviço por ID",
        description = "Retorna um serviço específico pelo seu identificador"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Servico> buscarPorId(
            @Parameter(
                description = "Identificador do serviço",
                example = "1",
                required = true
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar serviço",
        description = "Atualiza todos os dados de um serviço existente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Servico> atualizar(
            @Parameter(
                description = "Identificador do serviço",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody Servico servico) {

        return ResponseEntity.ok(
                servicoService.atualizar(id, servico)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir serviço",
        description = "Exclui um serviço pelo seu identificador"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Void> excluir(
            @Parameter(
                description = "Identificador do serviço",
                example = "1",
                required = true
            )
            @PathVariable Long id) {

        servicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}