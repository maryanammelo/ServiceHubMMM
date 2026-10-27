package com.servicehub.servicehub_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import com.jayway.jsonpath.JsonPath;
import com.servicehub.servicehub_api.model.*;
import com.servicehub.servicehub_api.repository.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class AuthIntegrationTest {
    private static final String SERVICO = """
        {"nome":" Manutenção ","descricao":"Limpeza de computadores","categoria":"Tecnologia","preco":150.00,"disponivel":true}
        """;
    private static final String CADASTRO = """
        {"nome":"Cliente Teste","email":" CLIENTE@teste.local ","senha":" senha-segura ","role":"ADMIN"}
        """;
    private MockMvc mvc;
    @Autowired WebApplicationContext context;
    @Autowired UsuarioRepository users;
    @Autowired ServicoRepository servicos;
    @Autowired PasswordEncoder encoder;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        servicos.deleteAll();
        users.deleteAll();
    }

    @Test void cadastroNaoElevaPrivilegioEPreservaSenha() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(CADASTRO))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("CLIENT"))
            .andExpect(jsonPath("$.email").value("cliente@teste.local"))
            .andExpect(jsonPath("$.senha").doesNotExist());
        var user = users.findByEmailIgnoreCase("cliente@teste.local").orElseThrow();
        assertNotEquals(" senha-segura ", user.getSenha());
        assertTrue(encoder.matches(" senha-segura ", user.getSenha()));
        assertFalse(encoder.matches("senha-segura", user.getSenha()));
        String token = login("cliente@teste.local", " senha-segura ");
        mvc.perform(post("/api/servicos").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(SERVICO)).andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(CADASTRO))
            .andExpect(status().isConflict());
    }

    @Test void credenciaisIncorretasRetornam401() throws Exception {
        seed(Role.CLIENT);
        for (String email : new String[]{"client@teste.local", "inexistente@teste.local"}) {
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        }
    }

    @Test void ausenciaDeTokenRetorna401EGetPublico200() throws Exception {
        mvc.perform(get("/api/servicos")).andExpect(status().isOk());
        mvc.perform(post("/api/servicos").contentType(MediaType.APPLICATION_JSON).content(SERVICO))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        mvc.perform(delete("/api/servicos/1")).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest @EnumSource(Role.class)
    void permissoesDeCriacaoAtualizacaoExclusao(Role role) throws Exception {
        seed(role);
        String token = login(role.name().toLowerCase() + "@teste.local", "senha-segura");
        var entity = new Servico();
        entity.setNome("Original"); entity.setDescricao("Descrição"); entity.setCategoria("Categoria");
        entity.setPreco(new java.math.BigDecimal("10.00")); entity.setDisponivel(true);
        long id = servicos.saveAndFlush(entity).getId();
        boolean writer = role != Role.CLIENT;
        mvc.perform(post("/api/servicos").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(SERVICO))
            .andExpect(status().is(writer ? 201 : 403));
        mvc.perform(put("/api/servicos/" + id).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(SERVICO))
            .andExpect(status().is(writer ? 200 : 403));
        mvc.perform(delete("/api/servicos/" + id).header("Authorization", "Bearer " + token))
            .andExpect(status().is(role == Role.ADMIN ? 204 : 403));
    }

    @Test void criarConsultarRestEGraphql() throws Exception {
        seed(Role.PROVIDER);
        String token = login("provider@teste.local", "senha-segura");
        String body = mvc.perform(post("/api/servicos").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(SERVICO))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.nome").value("Manutenção"))
            .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(body, "$.id");
        mvc.perform(get("/api/servicos/" + id)).andExpect(status().isOk())
            .andExpect(jsonPath("$.preco").value(150.0));
        mvc.perform(post("/graphql").contentType(MediaType.APPLICATION_JSON)
            .content("{\"query\":\"{ servicos { id nome } servico(id: " + id + ") { nome preco } }\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.errors").doesNotExist())
            .andExpect(jsonPath("$.data.servicos[0].nome").value("Manutenção"))
            .andExpect(jsonPath("$.data.servico.preco").value(150.0));
        assertEquals(1, servicos.count());
    }

    @Test void tokenInvalidoExpiradoEAssinaturaIncorretaRetornam401() throws Exception {
        seed(Role.ADMIN);
        var validKey = Keys.hmacShaKeyFor("teste-segredo-com-mais-de-32-caracteres-123456".getBytes(StandardCharsets.UTF_8));
        var wrongKey = Keys.hmacShaKeyFor("outra-chave-com-mais-de-32-caracteres-123456789".getBytes(StandardCharsets.UTF_8));
        String expired = Jwts.builder().subject("admin@teste.local").expiration(new Date(1)).signWith(validKey).compact();
        String forged = Jwts.builder().subject("admin@teste.local").expiration(new Date(System.currentTimeMillis()+60000)).signWith(wrongKey).compact();
        for (String token : new String[]{"invalido", expired, forged}) {
            mvc.perform(post("/api/servicos").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(SERVICO))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        }
    }

    @ParameterizedTest
    @ValueSource(strings={"{}", "{", "{\"nome\":\"ab\",\"descricao\":\"ok\",\"categoria\":\"ok\",\"preco\":0,\"disponivel\":true}",
        "{\"nome\":\"<script>alert(1)</script>\",\"descricao\":\"ok\",\"categoria\":\"ok\",\"preco\":10,\"disponivel\":true}",
        "{\"nome\":\"abc\",\"descricao\":\"ok\",\"categoria\":\"ok\",\"preco\":10.001,\"disponivel\":true}"})
    void rejeitaDadosInvalidos(String payload) throws Exception {
        seed(Role.PROVIDER);
        mvc.perform(post("/api/servicos").header("Authorization", "Bearer " + login("provider@teste.local", "senha-segura"))
            .contentType(MediaType.APPLICATION_JSON).content(payload)).andExpect(status().isBadRequest());
        assertEquals(0, servicos.count());
    }

    @Test void somenteAdminCriaUsuariosComPerfilElevado() throws Exception {
        seed(Role.ADMIN); seed(Role.CLIENT);
        mvc.perform(post("/api/usuarios").header("Authorization", "Bearer " + login("client@teste.local", "senha-segura"))
            .contentType(MediaType.APPLICATION_JSON).content(CADASTRO)).andExpect(status().isForbidden());
        mvc.perform(post("/api/usuarios").header("Authorization", "Bearer " + login("admin@teste.local", "senha-segura"))
            .contentType(MediaType.APPLICATION_JSON).content(CADASTRO))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("ADMIN"))
            .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test void recursoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/servicos/999999")).andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
        mvc.perform(get("/api/servicos/abc")).andExpect(status().isBadRequest());
    }

    private void seed(Role role) {
        var user = new Usuario(); user.setNome(role.name());
        user.setEmail(role.name().toLowerCase() + "@teste.local");
        user.setSenha(encoder.encode("senha-segura")); user.setRole(role); users.saveAndFlush(user);
    }

    private String login(String email, String senha) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.tipo").value("Bearer"))
            .andExpect(jsonPath("$.expiresInSeconds").value(3600))
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }
}
