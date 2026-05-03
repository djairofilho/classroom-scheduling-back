# Endpoints da API

## Visao geral

Base local:

```text
http://localhost:8080
```

Contrato OpenAPI exportado:

- [OpenAPI JSON](openapi.json)

As rotas protegidas exigem:

```http
Authorization: Bearer <token>
```

## Health

### `GET /health`

Retorna o status basico da aplicacao.

## Auth

### `POST /auth/register`

Cria um `Usuario` com `papel = SOLICITANTE` usando apenas email e senha.

Exemplo de body:

```json
{
  "email": "aluno@al.insper.edu.br",
  "senha": "senha123"
}
```

Dominios aceitos:

- `@al.insper.edu.br`: `tipoSolicitante = ALUNO`
- `@insper.edu.br`: `tipoSolicitante = FUNCIONARIO`

### `POST /auth/login`

Autentica usuario e retorna JWT.

Exemplo de body:

```json
{
  "email": "admin@insper.edu.br",
  "senha": "admin1234"
}
```

### `GET /auth/me`

Retorna o usuario autenticado a partir do JWT.

## Usuarios

### `GET /usuarios`

Lista todos os usuarios.

### `GET /usuarios/{id}`

Busca um usuario por id.

### `GET /usuarios/buscar?email=...`

Busca um usuario por email.

### `DELETE /usuarios/{id}`

Remove um usuario por id.

## Solicitantes

### `GET /solicitantes`

Lista usuarios com `papel = SOLICITANTE`.

### `GET /solicitantes/{id}`

Busca um usuario solicitante por id.

### `GET /solicitantes/buscar?email=...`

Busca um usuario solicitante por email.

### `POST /solicitantes`

Cria um usuario solicitante.

Exemplo de body:

```json
{
  "nome": "Maria Souza",
  "email": "maria@insper.edu.br"
}
```

## Predios

### `GET /predios`

Lista todos os predios.

### `GET /predios/{id}`

Busca um predio por id.

### `GET /predios/buscar?codigo=...`

Busca um predio por codigo.

### `POST /predios`

Cria um predio.

Exemplo de body:

```json
{
  "nome": "Predio A",
  "codigo": "A",
  "localizacao": "Campus principal"
}
```

## Espacos

### `GET /espacos`

Lista todos os espacos.

### `GET /espacos/{id}`

Busca um espaco por id.

### `GET /espacos/disponiveis`

Lista espacos com `indisponivel = false`.

### `GET /espacos/por-predio?predioId=...`

Lista os espacos vinculados a um predio.

### `POST /espacos`

Cria um espaco com tipo.

Exemplo de body:

```json
{
  "nome": "Sala B201",
  "tipo": "SALA",
  "capacidade": 40,
  "predioId": 1
}
```

Tipos aceitos:

- `SALA`
- `AUDITORIO`
- `QUADRA`
- `LABORATORIO`

### `PATCH /espacos/{id}/indisponibilidade`

Marca ou remove indisponibilidade de um espaco.

Exemplo de body:

```json
{
  "indisponivel": true,
  "motivo": "Manutencao preventiva"
}
```

## Reservas

### `GET /reservas`

Lista todas as reservas.

### `GET /reservas/{id}`

Busca uma reserva por id.

### `GET /reservas/ativas`

Lista reservas nao canceladas.

### `GET /reservas/por-solicitante?solicitanteId=...`

Lista reservas de um usuario solicitante. Usuarios com `papel = SOLICITANTE` so podem consultar o proprio id; `ADMIN` pode consultar qualquer id.

### `POST /reservas`

Cria uma reserva. Para usuarios solicitantes, o backend usa o id do usuario autenticado, mesmo que `solicitanteId` venha no body. Para admin, o `solicitanteId` informado e respeitado e pode ser o proprio admin.

Exemplo de body:

```json
{
  "solicitanteId": 2,
  "espacoId": 1,
  "inicio": "2030-05-10T10:00:00",
  "fim": "2030-05-10T12:00:00",
  "motivo": "Apresentacao de projeto"
}
```

### `PATCH /reservas/{id}/cancelar`

Cancela uma reserva.

## Notificacoes

### `GET /notificacoes`

Lista todas as notificacoes.

### `GET /notificacoes/{id}`

Busca uma notificacao por id.

### `GET /notificacoes/por-destinatario?destinatarioId=...`

Lista notificacoes de um destinatario.

### `GET /notificacoes/nao-lidas?destinatarioId=...`

Lista notificacoes nao lidas de um destinatario.

### `POST /notificacoes`

Cria uma notificacao usando referencias por objeto.

Exemplo de body:

```json
{
  "destinatario": {
    "id": 2
  },
  "reserva": {
    "id": 1
  },
  "mensagem": "Sua reserva foi confirmada"
}
```

### `PATCH /notificacoes/{id}/lida`

Marca uma notificacao como lida.

## Seed local

Com a seed local habilitada, a aplicacao sobe com dados basicos para demonstracao:

- `usuarioId = 1` normalmente representa o admin padrao
- `predioId = 1` e `predioId = 2`
- `espacoId = 1` para `Sala 101`
- `solicitanteId = 2` normalmente representa `ana.souza@al.insper.edu.br`
- `destinatarioId = 2` normalmente representa notificacoes da Ana
- `reservaId = 1` para a reserva de exemplo
- horarios de funcionamento sao criados para predios e espacos de demonstracao
- recursos de espaco e uma politica padrao de reserva sao criados para desenvolvimento local

Os IDs podem variar caso o banco ja tenha dados criados antes da seed.

## Respostas esperadas

- `200 OK` para consultas e atualizacoes
- `201 Created` para criacao de recursos
- `400 Bad Request` para dados invalidos ou violacao de regra
- `404 Not Found` para recurso inexistente
