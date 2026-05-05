# classroom-scheduling-back

Backend Spring Boot para o frontend `classroom-scheduling-front`.

## Base

- URL local: `http://localhost:8080`
- Auth header: `Authorization: Bearer <token>`
- `Content-Type: application/json`

## Rodar

```powershell
.\mvnw clean spring-boot:run
```

## Endpoints suportados (PT e EN)

### Auth
- `POST /auth/login` e `POST /auth/sessions`
- `POST /auth/register` e `POST /auth/users`
- `GET /auth/me`
- Request de login/register:
  - `{ "email": "string", "senha": "string" }`
- Registro publico:
  - cria apenas solicitante (nunca admin)
  - email deve ser institucional (`@al.insper.edu.br` ou `@insper.edu.br`)

### Health
- `GET /health` -> `{ "status": "UP", "timestamp": "ISO-8601" }`

### Buildings
- `GET /predios`, `GET /buildings`
- `GET /predios/{id}`, `GET /buildings/{id}`
- `POST /predios`, `POST /buildings`
- `PUT /predios/{id}`, `PUT /buildings/{id}`
- `DELETE /predios/{id}`, `DELETE /buildings/{id}`
- Busca por codigo: `GET /predios?codigo=B1` ou `GET /buildings?code=B1`

### Spaces
- `GET /espacos`, `GET /spaces`
- `GET /espacos/{id}`, `GET /spaces/{id}`
- `POST /espacos`, `POST /spaces`
- `PUT /espacos/{id}`, `PUT /spaces/{id}`
- `DELETE /espacos/{id}`, `DELETE /spaces/{id}`
- `PATCH /espacos/{id}/indisponibilidade`, `PATCH /spaces/{id}/availability`
- Filtros:
  - `GET /spaces?available=true`
  - `GET /spaces?buildingId=1` (ou `predioId=1`)

### Reservations
- `GET /reservas`, `GET /reservations`
- `GET /reservas/ativas`, `GET /reservations?status=ACTIVE`
- `GET /reservas/{id}`, `GET /reservations/{id}`
- `POST /reservas`, `POST /reservations`
- `PATCH /reservas/{id}/cancelar`, `PATCH /reservations/{id}/cancel`
- `PATCH /reservas/{id}/aprovar`, `PATCH /reservations/{id}/approve`
- `PATCH /reservas/{id}/recusar`, `PATCH /reservations/{id}/reject`
- `POST /reservas/lote`, `POST /reservations/bulk`
- disponibilidade por espaco/dia:
  - `GET /reservas/por-espaco?espacoId=7&data=2026-05-10`
- Filtros:
  - `GET /reservations?status=ACTIVE`
  - `GET /reservations?requesterId=10` (ou `solicitanteId=10`)
  - `GET /reservations?spaceId=7&date=2026-05-10`
  - `GET /reservations?spaceId=7&from=2026-05-10T14:00:00&to=2026-05-10T16:00:00`
- Body de lote:
  - `{ "solicitanteId": 10, "espacoId": 7, "dataInicio": "2026-05-01", "dataFim": "2026-06-30", "diasSemana": [1,2,3,4,5], "horaInicio": "19:00", "horaFim": "21:00", "motivo": "string" }`
- Response de lote:
  - `{ "quantidadeCriada": 24, "quantidadeIgnorada": 2 }`
- Status de reserva:
  - `PENDENTE`, `APROVADA`, `RECUSADA`, `CANCELADA`

### Notifications
- `GET /notificacoes`, `GET /notifications`
- `GET /notificacoes/{id}`, `GET /notifications/{id}`
- `PATCH /notificacoes/{id}/lida`, `PATCH /notifications/{id}/read`
- Filtros:
  - `GET /notifications?recipientId=10` (ou `destinatarioId=10`)
  - `GET /notifications?recipientId=10&read=false`

### Users / Requesters
- `GET /usuarios`, `GET /users`
- `GET /usuarios/{id}`, `GET /users/{id}`
- `GET /usuarios?email=x@y.com`, `GET /users?email=x@y.com`
- `PUT /usuarios/{id}`, `PUT /users/{id}`
- `PATCH /usuarios/{id}/status`, `PATCH /users/{id}/status`
- `DELETE /usuarios/{id}`, `DELETE /users/{id}`

- `GET /solicitantes`, `GET /requesters`
- `GET /solicitantes/{id}`, `GET /requesters/{id}`
- `GET /solicitantes?email=x@y.com`, `GET /requesters?email=x@y.com`
- `POST /solicitantes`, `POST /requesters`
- `DELETE /solicitantes/{id}`, `DELETE /requesters/{id}`

## Permissoes

- Usuario autenticado (solicitante):
  - pode criar/cancelar suas reservas
  - pode consultar disponibilidade por `GET /reservas/por-espaco`
  - pode acessar `GET /reservas/ativas`
- Admin:
  - pode aprovar/recusar reservas
  - pode criar reservas em lote
  - pode excluir espacos/predios
  - pode acessar listagens administrativas de usuarios/solicitantes/reservas

## Erros (formato)

```json
{
  "timestamp": "2026-05-05T12:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "User role not allowed for this endpoint",
  "path": "/reservas/por-espaco"
}
```
