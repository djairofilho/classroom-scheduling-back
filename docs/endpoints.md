# Endpoints da API

## Visao geral

Os endpoints abaixo representam a API planejada para o `Classroom Scheduler`, organizada em torno dos casos de uso principais do sistema.

Base local:

```text
http://localhost:8080
```

## Salas

### `GET /salas`

Lista todas as salas cadastradas.

Uso principal:

- consulta geral de salas
- apoio para tela de listagem

### `GET /salas/disponiveis`

Lista salas disponiveis para um intervalo e filtros informados.

Parametros sugeridos:

- `inicio`
- `fim`
- `capacidadeMinima`
- `recurso`

Exemplo:

```text
GET /salas/disponiveis?inicio=2026-05-10T10:00:00&fim=2026-05-10T12:00:00&capacidadeMinima=30
```

### `POST /salas`

Cria uma nova sala. Endpoint administrativo.

Exemplo de body:

```json
{
  "nome": "Sala B201",
  "capacidade": 40,
  "predioId": 1,
  "recursoIds": [1, 2]
}
```

## Reservas

### `POST /reservas`

Cria uma reserva para um usuario e uma sala em um horario informado.

Exemplo de body:

```json
{
  "usuarioId": 2,
  "salaId": 5,
  "inicio": "2026-05-10T10:00:00",
  "fim": "2026-05-10T12:00:00",
  "motivo": "Estudo em grupo"
}
```

### `GET /reservas/minhas`

Lista as reservas do usuario autenticado.

Uso principal:

- consulta de reservas ativas
- historico de reservas

### `PUT /reservas/{id}/cancelar`

Cancela uma reserva.

Regras:

- usuario comum cancela apenas a propria reserva
- administrador pode cancelar qualquer reserva

Exemplo de path:

```text
PUT /reservas/10/cancelar
```

## Manutencao

### `POST /salas/{id}/manutencoes`

Cria um bloqueio de manutencao para a sala. Endpoint administrativo.

Exemplo de body:

```json
{
  "inicio": "2026-05-11T08:00:00",
  "fim": "2026-05-11T18:00:00",
  "motivo": "Manutencao do projetor"
}
```

### `DELETE /salas/{id}/manutencoes/{manutencaoId}`

Remove um bloqueio de manutencao da sala. Endpoint administrativo.

## Perfis de acesso

- Usuario comum: consulta salas, consulta disponibilidade, cria reserva, cancela a propria reserva, lista suas reservas
- Administrador: todas as operacoes de usuario comum, mais cadastro de salas e gestao de manutencao

## Respostas esperadas

- `200 OK`: consulta ou atualizacao realizada com sucesso
- `201 Created`: recurso criado com sucesso
- `400 Bad Request`: violacao de regra de negocio ou dados invalidos
- `404 Not Found`: sala, usuario, reserva ou manutencao inexistente
