# Relatório ServiceHub aulas 8 a 10

## 1. Identificação

**Repositório:** [ServiceHubMMM](https://github.com/maryanammelo/ServiceHubMMM)

**Último commit registrado:** `9d13746b3df532ad2c6762429505be15cf71a75c` — Initial commit.

**Entrega local verificada em:** 27/10/2026. As implementações descritas estão na árvore de trabalho e ainda não possuem novo commit nem envio confirmado ao GitHub.

## 2. Estado das entregas

### Aula 8 · Segurança e validação: implementada e verificada automaticamente

A API utiliza `SecurityFilterChain` sem sessão, senhas com BCrypt, DTOs de entrada e saída e tratamento global de erros. Os textos são normalizados antes da validação; marcação HTML e caracteres de controle são rejeitados. Os limites de tamanho e precisão acompanham as colunas do banco. As operações protegidas usam `@PreAuthorize`.

**Como comprovei:** testes MockMvc verificaram dados inválidos e JSON malformado com retorno 400, serviço inexistente com 404 e email duplicado com 409. Os testes também confirmaram a persistência do hash BCrypt, a preservação dos espaços da senha e a ausência de senha na resposta do cadastro. Os testes de autorização aplicam o filtro real do Spring Security.

### Aula 9 · JWT, papéis e endpoint alternativo: implementada e verificada automaticamente

`POST /api/auth/login` autentica credenciais e emite JWT assinado com expiração configurável. O filtro verifica assinatura e validade e carrega as permissões atuais no banco. Os perfis são `ADMIN`, `PROVIDER` e `CLIENT`.

GET de serviços é público. POST e PUT exigem ADMIN ou PROVIDER; DELETE exige ADMIN. O cadastro público sempre cria CLIENT. A criação de usuários com perfil elevado ocorre em `POST /api/usuarios`, restrito a ADMIN. O README explica a promoção controlada do primeiro administrador por acesso direto ao banco.

A alternativa implementada é GraphQL: `POST /graphql`, com consultas `servicos` e `servico(id)`, reutilizando `ServicoService`.

**Como comprovei:** testes de integração verificaram login válido, credenciais incorretas, ausência de token, token malformado, expirado e assinado com outra chave. Verificaram também criação, atualização e exclusão para cada um dos três perfis, com respostas 201/200/204 ou 403 conforme a permissão. Um teste criou um serviço autenticado e consultou os mesmos dados por REST e GraphQL.

### Aula 10 · Testes: implementada e executada

| Suíte | Testes | Escopo |
|---|---:|---|
| `ServicoServiceTest` | 6 | Mockito: criação, atualização, exclusão e recursos inexistentes |
| `ServicoControllerTest` | 5 | MockMvc isolado: contratos de listagem, criação, validação, 404 e exclusão |
| `AuthIntegrationTest` | 15 | Contexto Spring, filtros de segurança, JWT, perfis, persistência e GraphQL |
| `ServicehubApiApplicationTests` | 1 | Inicialização do contexto |
| **Total** | **27** | **0 falhas, 0 erros, 0 ignorados** |

**Como comprovei:** `clean verify` concluiu com `BUILD SUCCESS` e gerou o JAR executável. A execução usou Java 21.0.9, Maven Wrapper e H2 em modo PostgreSQL. Os resumos estão em [evidencias](evidencias/). H2 comprova o fluxo automatizado nesta configuração; a homologação em PostgreSQL permanece pendente.

## 3. Decisão técnica e justificativa

Foi mantida a separação controller, service e repository para permitir testes isolados de regras e compartilhamento da lógica entre REST e GraphQL. Os DTOs limitam os dados aceitos e retornados. O cadastro público com CLIENT aplica o menor privilégio e impede autoatribuição de ADMIN. A consulta ao perfil atual no banco permite aplicar alterações de permissão a tokens ainda válidos.

Os fundamentos foram consultados no Atlas, especialmente a distinção entre autenticação e autorização, defesa em profundidade, menor privilégio e níveis de teste. O material específico do professor para as aulas 8 a 10 de backend não foi localizado; o escopo desta entrega segue o relatório fornecido.

## 4. Dificuldade ou erro e como resolvemos

Além do histórico de `JAVA_HOME` relatado anteriormente, a verificação desta entrega revelou que o Maven Wrapper indexava uma propriedade `Target` nula em uma pasta comum no Windows. O script foi corrigido para tratar a ausência de link simbólico e então utilizou o Maven já instalado no cache.

Também foi identificado que o teste de autenticação anterior construía MockMvc sem a integração com Spring Security. A suíte passou a aplicar `springSecurity()`, exercitando autenticação e autorização reais. O empacotamento final passou com todos os 27 testes.

## 5. Próximos passos

1. Configurar as variáveis de ambiente e executar a aplicação com PostgreSQL.
2. Seguir o roteiro do README para criar ADMIN e PROVIDER e capturar as respostas no Swagger, sem divulgar senha ou token.
3. Revisar as alterações locais, registrar um novo commit e enviar ao GitHub. O workflow de verificação já está preparado; sua execução remota ainda não foi comprovada.
4. Confirmar eventuais requisitos adicionais do enunciado original com o professor.

## Referências consultadas

- Atlas: `faculdade/aulas/aula-04-tipos-de-testes-software.md` — testes isolados, mocks e integração.
- Atlas: `faculdade/consolidado/md/aulas/aula-17/pdfs/02. Aula 17 - Resumo do Aluno - OAuth2 JWT e Governança de APIs.md` — identidade, autorização e menor privilégio.
- [Spring Security com MockMvc](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/setup.html).
- [Spring for GraphQL no Spring Boot](https://docs.spring.io/spring-boot/reference/web/spring-graphql.html).

Os caminhos do Atlas são relativos a `C:\Users\Maryana.Melo\OneDrive\Documentos\Atlas`.
