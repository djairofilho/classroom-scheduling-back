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
- `GET /reservas/{id}`, `GET /reservations/{id}`
- `POST /reservas`, `POST /reservations`
- `PATCH /reservas/{id}/cancelar`, `PATCH /reservations/{id}/cancel`
- Filtros:
  - `GET /reservations?status=ACTIVE`
  - `GET /reservations?requesterId=10` (ou `solicitanteId=10`)
  - `GET /reservations?spaceId=7&from=2026-05-10T14:00:00&to=2026-05-10T16:00:00`

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
