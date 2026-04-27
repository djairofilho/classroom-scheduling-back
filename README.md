# Classroom Scheduler

Sistema para agendamento de salas da faculdade, com foco em evitar conflitos de horario e manter as regras de reserva bem definidas no dominio.

## Objetivo

O projeto organiza o processo de reserva de salas academicas de forma simples e clara. A proposta prioriza:

- modelagem de dominio bem estruturada
- separacao de responsabilidades entre camadas
- regras de negocio centralizadas
- API REST para operacoes principais de consulta e reserva

## Escopo funcional

### Usuario comum

- Ver salas disponiveis
- Filtrar salas por capacidade
- Criar reserva
- Cancelar a propria reserva
- Consultar suas reservas

### Administrador

- Criar salas
- Definir capacidade
- Adicionar recursos
- Bloquear salas para manutencao
- Cancelar qualquer reserva

## Dominio do problema

O sistema foi planejado para destacar conceitos importantes de orientacao a objetos e modelagem:

- Heranca em `Usuario`, com especializacoes `Aluno`, `Professor` e `Administrador`
- Composicao entre `Reserva` e `Horario`
- Associacoes entre `Sala`, `Predio`, `Recurso` e `Manutencao`
- Regras encapsuladas em `PoliticaReserva`
- Estados da reserva representados por `StatusReserva`

Os principais elementos do dominio sao:

- `Usuario`
- `Aluno`
- `Professor`
- `Administrador`
- `Sala`
- `Predio`
- `Recurso`
- `Reserva`
- `Horario`
- `Manutencao`
- `PoliticaReserva`
- `StatusReserva`
- `TipoUsuario`

## Regras de negocio centrais

- Nao pode existir reserva em conflito de horario para a mesma sala
- Nao pode reservar sala bloqueada por manutencao
- O horario deve ser valido, com fim posterior ao inicio
- Nao pode criar reserva no passado
- Aluno possui limite de duracao da reserva
- Professor possui maior flexibilidade de reserva
- Administrador pode cancelar qualquer reserva

## Arquitetura

O projeto segue uma arquitetura em camadas com Spring Boot:

- `controller`: recebe e responde requisicoes HTTP
- `service`: aplica regras de negocio e coordenacao de casos de uso
- `repository`: acesso aos dados com Spring Data JPA
- `model`: entidades, enums e objetos de valor do dominio
- `dto`: contratos de entrada e saida da API
- `exception`: tratamento padronizado de erros

Estrutura atual do codigo:

```text
src/main/java/com/classroomscheduler
|-- controller
|-- dto
|-- exception
|-- model
|-- repository
|-- service
`-- ApiApplication.java
```

## Endpoints planejados

### Salas

- `GET /salas`
- `GET /salas/disponiveis`
- `POST /salas` (admin)

### Reservas

- `POST /reservas`
- `GET /reservas/minhas`
- `PUT /reservas/{id}/cancelar`

### Apoio administrativo

- `POST /salas/{id}/manutencoes`
- `DELETE /salas/{id}/manutencoes/{manutencaoId}`

Os detalhes de contratos e exemplos estao em [docs/endpoints.md](/C:/Users/Usuario/Documents/Insper/arq_obj/Agendamento/docs/endpoints.md).

## Como executar

Pre-requisitos:

- JDK instalado
- `JAVA_HOME` configurado
- Java 25, conforme `pom.xml`

Executar a aplicacao:

```powershell
.\mvnw spring-boot:run
```

Executar testes:

```powershell
.\mvnw test
```

Aplicacao local:

```text
http://localhost:8080
```

Documentacao OpenAPI / Swagger UI:

```text
http://localhost:8080/docs/swagger-ui.html
```

## Banco de dados

O projeto esta configurado com H2 em memoria em [src/main/resources/application.properties](/C:/Users/Usuario/Documents/Insper/arq_obj/Agendamento/src/main/resources/application.properties).

- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Senha: em branco
- Console H2 habilitado

Console H2:

```text
http://localhost:8080/h2-console
```

Como o banco esta em memoria, os dados sao perdidos ao encerrar a aplicacao.

## Documentacao complementar

- [Modelagem de dominio](/C:/Users/Usuario/Documents/Insper/arq_obj/Agendamento/docs/modelagem-de-dominio.md)
- [Regras de negocio](/C:/Users/Usuario/Documents/Insper/arq_obj/Agendamento/docs/regras-de-negocio.md)
- [Endpoints da API](/C:/Users/Usuario/Documents/Insper/arq_obj/Agendamento/docs/endpoints.md)
- [Collection Postman](/C:/Users/Usuario/Documents/Insper/arq_obj/Agendamento/docs/api-collection.postman_collection.json)

## Observacao

Esta documentacao descreve o dominio e a API planejados para o projeto `Classroom Scheduler`, mesmo que a implementacao atual ainda esteja em transicao a partir de um template anterior.
