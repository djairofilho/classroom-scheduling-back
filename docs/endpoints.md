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

Cria um novo espaco concreto. Endpoint administrativo e util para montar o fluxo completo de testes.

Exemplo de body:

```json
{
  "nome": "Laboratorio C204",
  "tipo": "LABORATORIO",
  "capacidade": 40,
  "predioId": 1
}
```

Tipos aceitos em `tipo`:

- `SALA`
- `AUDITORIO`
- `QUADRA`
- `LABORATORIO`

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

Cria uma reserva para um `Solicitante` em um `Espaco` usando um body simples com IDs e intervalo.

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

### `GET /reservas`

Lista todas as reservas cadastradas.

### `GET /reservas/ativas`

Lista apenas as reservas nao canceladas.

### `GET /reservas/por-solicitante`

Lista as reservas de um solicitante pelo `solicitanteId`.

Exemplo:

```text
GET /reservas/por-solicitante?solicitanteId=2
```

### `PATCH /reservas/{id}/cancelar`

Cancela uma reserva.

Regras:

- `Solicitante` cancela apenas a propria reserva
- `Admin` pode cancelar qualquer reserva

Exemplo de path:

```text
PUT /reservas/10/cancelar
```

## Solicitantes

### `GET /solicitantes`

Lista os solicitantes cadastrados.

### `GET /solicitantes/{id}`

Busca um solicitante por identificador.

### `GET /solicitantes/buscar`

Busca um solicitante por email.

Exemplo:

```text
GET /solicitantes/buscar?email=aluno@insper.edu.br
```

### `POST /solicitantes`

Cria um solicitante para uso no fluxo de reserva.

Exemplo de body:

```json
{
  "nome": "Maria Souza",
  "email": "maria@insper.edu.br"
}
```

## Admin padrao

O `Admin` nao possui endpoint de criacao publica neste ajuste. A aplicacao cria um administrador padrao ao subir, usando variaveis de ambiente:

- `APP_ADMIN_NOME`
- `APP_ADMIN_EMAIL`

Valores default quando nao configurados:

- nome: `Administrador Padrao`
- email: `admin@classroom.local`

## Notificacoes

### `GET /notificacoes`

Lista todas as notificacoes.

### `POST /notificacoes`

Cria uma notificacao. Em geral este endpoint pode ser interno ou administrativo, dependendo da arquitetura adotada.

Exemplo de body:

```json
{
  "destinatarioId": 2,
  "reservaId": 10,
  "mensagem": "Sua reserva foi confirmada"
}
```

### `PATCH /notificacoes/{id}/lida`

Marca uma notificacao como lida.

## Perfis de acesso

- `Solicitante`: criado via API para testar reservas, consultar espacos, criar reserva, listar reservas e consultar notificacoes
- `Admin`: bootstrapado por ambiente, sem criacao publica via API, e reservado para acoes administrativas

## Respostas esperadas

- `200 OK`: consulta ou atualizacao realizada com sucesso
- `201 Created`: recurso criado com sucesso
- `400 Bad Request`: violacao de regra de negocio ou dados invalidos
- `404 Not Found`: espaco, usuario, reserva, predio ou notificacao inexistente
