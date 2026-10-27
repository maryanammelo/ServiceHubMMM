package com.servicehub.servicehub_api.controler;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.util.List;
import com.servicehub.servicehub_api.exception.GlobalExceptionHandler;
import com.servicehub.servicehub_api.model.Servico;
import com.servicehub.servicehub_api.service.ServicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// Teste isolado do contrato MVC. Os filtros e papéis são exercitados em AuthIntegrationTest.
class ServicoControllerTest {
    private ServicoService service;
    private MockMvc mvc;
    @BeforeEach void setup() {
        service = mock(ServicoService.class);
        mvc = MockMvcBuilders.standaloneSetup(new ServicoController(service))
            .setControllerAdvice(new GlobalExceptionHandler()).build();
    }
    @Test void deveListarDadosDoServico() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(servico()));
        mvc.perform(get("/api/servicos")).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].nome").value("Manutenção"));
        verify(service).listarTodos();
    }
    @Test void deveCriarComDtoERetornar201() throws Exception {
        when(service.criar(any())).thenReturn(servico());
        mvc.perform(post("/api/servicos").contentType(MediaType.APPLICATION_JSON).content("""
            {"nome":"Manutenção","descricao":"Descrição","categoria":"Tecnologia","preco":150,"disponivel":true}
            """))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
    }
    @Test void deveRejeitarDadosInvalidosSemChamarService() throws Exception {
        mvc.perform(post("/api/servicos").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fields.nome").exists());
        verifyNoInteractions(service);
    }
    @Test void deveRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"Serviço não encontrado"));
        mvc.perform(get("/api/servicos/99")).andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Serviço não encontrado"));
    }
    @Test void deveExcluirERetornar204() throws Exception {
        mvc.perform(delete("/api/servicos/1")).andExpect(status().isNoContent());
        verify(service).excluir(1L);
    }
    private Servico servico() {
        var s = new Servico(); s.setId(1L); s.setNome("Manutenção"); s.setDescricao("Descrição");
        s.setCategoria("Tecnologia"); s.setPreco(new BigDecimal("150.00")); s.setDisponivel(true); return s;
    }
}
