# Endpoints da API

## Visao geral

Base local:

```text
http://localhost:8080
```

Contrato OpenAPI exportado:

- [OpenAPI JSON](/C:/Users/Usuario/Documents/Insper/arq_obj/Agendamento/docs/openapi.json)

## Health

### `GET /health`

Retorna o status basico da aplicacao.

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

Lista todos os solicitantes.

### `GET /solicitantes/{id}`

Busca um solicitante por id.

### `GET /solicitantes/buscar?email=...`

Busca um solicitante por email.

### `POST /solicitantes`

Cria um solicitante.

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

Cria um espaco concreto.

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

Lista reservas de um solicitante.

### `POST /reservas`

Cria uma reserva.

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

- `usuarioId = 1` para o admin padrao
- `predioId = 1` e `predioId = 2`
- `espacoId = 1` para `Sala 101`
- `solicitanteId = 2` para `Ana Souza`
- `destinatarioId = 2` para notificacoes da Ana
- `reservaId = 1` para a reserva de exemplo

## Respostas esperadas

- `200 OK` para consultas e atualizacoes
- `201 Created` para criacao de recursos
- `400 Bad Request` para dados invalidos ou violacao de regra
- `404 Not Found` para recurso inexistente
