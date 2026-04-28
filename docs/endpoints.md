# Endpoints da API

## Visao geral

Os endpoints abaixo representam a API planejada para o `Classroom Scheduler`, organizada em torno das classes obrigatorias do dominio.

Base local:

```text
http://localhost:8080
```

## Espacos

### `GET /espacos`

Lista todos os espacos cadastrados.

Uso principal:

- consulta geral de espacos
- apoio para tela de listagem

### `GET /espacos/disponiveis`

Lista espacos disponiveis para um intervalo e filtros informados.

Parametros sugeridos:

- `inicio`
- `fim`
- `capacidadeMinima`
- `tipo`
- `predioId`

Exemplo:

```text
GET /espacos/disponiveis?inicio=2026-05-10T10:00:00&fim=2026-05-10T12:00:00&capacidadeMinima=30&tipo=AUDITORIO
```

### `POST /espacos`

Cria um novo espaco. Endpoint administrativo.

Exemplo de body:

```json
{
  "nome": "Laboratorio C204",
  "tipo": "LABORATORIO",
  "capacidade": 40,
  "predioId": 1
}
```

### `PATCH /espacos/{id}/indisponibilidade`

Marca ou remove indisponibilidade de um espaco. Endpoint administrativo.

Exemplo de body:

```json
{
  "indisponivel": true,
  "motivo": "Manutencao preventiva"
}
```

## Reservas

### `POST /reservas`

Cria uma reserva para um `Solicitante` em um `Espaco` com um `Horarios` informado.

Exemplo de body:

```json
{
  "solicitanteId": 2,
  "espacoId": 5,
  "inicio": "2026-05-10T10:00:00",
  "fim": "2026-05-10T12:00:00",
  "motivo": "Apresentacao de projeto"
}
```

### `GET /reservas/minhas`

Lista as reservas do solicitante autenticado.

Uso principal:

- consulta de reservas ativas
- historico de reservas

### `PUT /reservas/{id}/cancelar`

Cancela uma reserva.

Regras:

- `Solicitante` cancela apenas a propria reserva
- `Admin` pode cancelar qualquer reserva

Exemplo de path:

```text
PUT /reservas/10/cancelar
```

## Notificacoes

### `GET /notificacoes`

Lista notificacoes do usuario autenticado.

### `POST /notificacoes`

Cria uma notificacao. Em geral este endpoint pode ser interno ou administrativo, dependendo da arquitetura adotada.

Exemplo de body:

```json
{
  "usuarioId": 2,
  "reservaId": 10,
  "mensagem": "Sua reserva foi confirmada"
}
```

## Perfis de acesso

- `Solicitante`: consulta espacos, consulta disponibilidade, cria reserva, cancela a propria reserva, lista suas reservas e notificacoes
- `Admin`: todas as operacoes de `Solicitante`, mais cadastro de espacos e controle de indisponibilidade

## Respostas esperadas

- `200 OK`: consulta ou atualizacao realizada com sucesso
- `201 Created`: recurso criado com sucesso
- `400 Bad Request`: violacao de regra de negocio ou dados invalidos
- `404 Not Found`: espaco, usuario, reserva, predio ou notificacao inexistente
