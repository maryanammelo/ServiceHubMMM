# ServiceHub API

API Backend desenvolvida como checkpoint da disciplina de Desenvolvimento de API Backend.

O projeto permite cadastrar, listar, consultar, atualizar e excluir serviços.

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Bean Validation
- SpringDoc OpenAPI
- Swagger UI
- Maven

## Estrutura do projeto

O projeto está organizado nas seguintes camadas:

- Controller: recebe as requisições HTTP.
- Service: contém a lógica da aplicação.
- Repository: realiza a comunicação com o banco de dados.
- Model: representa a entidade Serviço.

## Funcionalidades

- Cadastrar um serviço
- Listar todos os serviços
- Buscar um serviço pelo ID
- Atualizar um serviço
- Excluir um serviço
- Validar os dados recebidos
- Retornar erro 404 quando o serviço não existe
- Documentar os endpoints pelo Swagger

## Endpoints

| Método | Endpoint | Função |
|---|---|---|
| POST | `/api/servicos` | Cadastrar um serviço |
| GET | `/api/servicos` | Listar os serviços |
| GET | `/api/servicos/{id}` | Buscar serviço pelo ID |
| PUT | `/api/servicos/{id}` | Atualizar um serviço |
| DELETE | `/api/servicos/{id}` | Excluir um serviço |

## Configuração do banco de dados

Crie no PostgreSQL um banco chamado:

```text
servicehub