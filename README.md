# ServiceHub API

API da disciplina de Desenvolvimento de API Backend, com CRUD de serviços, validação, Spring Security, BCrypt, JWT, três perfis e consulta alternativa em GraphQL.

## Tecnologias e arquitetura

Java 21, Spring Boot 4.1.1, Spring MVC, Spring Data JPA, PostgreSQL, Bean Validation, Spring Security, JJWT, Spring for GraphQL e SpringDoc OpenAPI. Testes com JUnit, Mockito, MockMvc e H2 no modo PostgreSQL.

Fluxo REST: controller → service → repository → banco. O controller GraphQL reutiliza `ServicoService`. DTOs definem os contratos de entrada e saída, sem expor senhas ou entidades JPA nas respostas REST.

## Executar no Windows

Tenha um JDK 21 configurado em `JAVA_HOME` e PostgreSQL acessível. Crie o banco antes de iniciar:

```sql
CREATE DATABASE servicehub;
```

No PowerShell, a partir da raiz deste repositório:

```powershell
$env:SERVICEHUB_DB_PASSWORD='sua-senha-do-postgres'
# Gere uma chave aleatória local de 32 bytes; não a publique nem a troque a cada reinício.
$env:SERVICEHUB_JWT_SECRET=[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
Set-Location .\servicehub-api
.\mvnw.cmd spring-boot:run
```

A configuração padrão usa `localhost:5432/servicehub`, usuário `postgres`, porta HTTP 8080 e JWT com validade de 60 minutos. `SERVICEHUB_JWT_EXPIRATION_MINUTES` altera a validade. As propriedades padrão de datasource do Spring podem ser substituídas por variáveis `SPRING_DATASOURCE_URL` e `SPRING_DATASOURCE_USERNAME`.

A chave deve conter pelo menos 32 caracteres. Senhas e chave JWT são fornecidas pelo ambiente. A configuração `ddl-auto=update` é destinada ao desenvolvimento; alterações de produção devem usar migrações versionadas.

## Endpoints e permissões

| Método | Endpoint | Acesso e resultado |
|---|---|---|
| POST | `/api/auth/register` | Público; cria sempre CLIENT, mesmo se receber `role: ADMIN` |
| POST | `/api/auth/login` | Público; retorna token Bearer, tipo e validade |
| POST | `/api/usuarios` | ADMIN; permite cadastrar CLIENT, PROVIDER ou ADMIN |
| GET | `/api/servicos` | Público; lista serviços |
| GET | `/api/servicos/{id}` | Público; consulta serviço |
| POST | `/api/servicos` | ADMIN ou PROVIDER; retorna 201 |
| PUT | `/api/servicos/{id}` | ADMIN ou PROVIDER; retorna 200 |
| DELETE | `/api/servicos/{id}` | ADMIN; retorna 204 |
| POST | `/graphql` | Público; somente consultas de serviços |

`@PreAuthorize` controla as operações protegidas. Nesta entrega, PROVIDER pode editar qualquer serviço; não existe vínculo de propriedade entre serviço e usuário. O filtro valida a assinatura e a expiração do JWT e consulta o perfil atual do usuário no banco a cada requisição.

### Primeiro administrador em desenvolvimento

1. Cadastre sua conta em `/api/auth/register`.
2. Com uma conexão administrativa ao PostgreSQL, promova somente essa conta:

```sql
UPDATE usuarios SET role = 'ADMIN' WHERE email = 'seu-email@exemplo.com';
```

3. Faça login e use o JWT para cadastrar prestadores em `/api/usuarios` com `role: PROVIDER`.

Essa etapa exige acesso administrativo direto ao banco. A aplicação não inclui senha padrão ou administrador automático.

## Exemplos no Swagger

Abra [Swagger UI](http://localhost:8080/swagger-ui.html). O contrato JSON está em [OpenAPI](http://localhost:8080/v3/api-docs).

Cadastro público:

```json
{"nome":"Maria","email":"maria@exemplo.com","senha":"uma-senha-segura"}
```

Login:

```json
{"email":"maria@exemplo.com","senha":"uma-senha-segura"}
```

Copie apenas o valor de `token` para **Authorize**: o esquema HTTP Bearer adiciona o prefixo automaticamente. Em outros clientes HTTP, use `Authorization: Bearer <token>`.

Cadastro ou atualização de serviço com um token ADMIN ou PROVIDER:

```json
{"nome":"Manutenção de computadores","descricao":"Limpeza e configuração","categoria":"Tecnologia","preco":150.00,"disponivel":true}
```

Os textos têm espaços externos removidos antes da validação e rejeitam `<`, `>` e caracteres de controle. O nome exige 3 a 100 caracteres, descrição até 500 e categoria até 80. O preço deve ser positivo, com até oito dígitos inteiros e duas casas decimais. Nome e email de usuário respeitam os limites das colunas. O email é normalizado para minúsculas; a senha é preservada e aceita 8 a 72 caracteres, limitada também a 72 bytes UTF-8 por causa do BCrypt.

Erros REST: 400 para validação ou JSON inválido, 401 para credenciais/token inválidos ou ausência de autenticação em rota protegida, 403 para falta de permissão, 404 para serviço inexistente e 409 para email duplicado. O corpo inclui `status` e `error`; falhas de validação incluem `fields`.

## Consulta GraphQL

Envie o JSON abaixo por POST a `http://localhost:8080/graphql`, com `Content-Type: application/json`:

```json
{"query":"{ servicos { id nome categoria preco disponivel } }"}
```

Para consultar um registro, use um ID retornado pela listagem:

```json
{"query":"query($id: ID!) { servico(id: $id) { id nome descricao preco } }","variables":{"id":"1"}}
```

O schema está em `servicehub-api/src/main/resources/graphql/schema.graphqls`. GraphQL usa o envelope `data`/`errors`; verifique `errors` mesmo com HTTP 200. Não há mutations nesta entrega.

## Testes e empacotamento

A partir de `servicehub-api`:

```powershell
.\mvnw.cmd clean verify
```

O perfil `test` é ativado nas classes de integração, usa H2 em memória e uma chave exclusiva de testes, sem exigir PostgreSQL ou segredos de produção. A suíte verifica o serviço com Mockito, os contratos MVC com MockMvc e os fluxos reais de autenticação, autorização, persistência e GraphQL com o filtro de segurança ativo. H2 não substitui uma homologação em PostgreSQL.

O JAR é gerado em `servicehub-api/target/servicehub-api-0.0.1-SNAPSHOT.jar`. O workflow `.github/workflows/build.yml` executa a mesma verificação no GitHub após envio do código.

Consulte [o relatório das aulas 8 a 10](docs/RELATORIO-AULAS-8-10.md) para as evidências e os limites da entrega. Não há capturas manuais novas do Swagger anexadas.
