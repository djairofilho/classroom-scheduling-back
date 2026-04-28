# Regras de Negocio

## Objetivo

As regras do `Classroom Scheduler` garantem que o sistema evite conflitos, respeite restricoes de uso e mantenha a reserva de espacos previsivel e segura.

## Regras obrigatorias

- Nao permitir reserva de um `Espaco` ja ocupado no mesmo intervalo
- Nao permitir reserva de um `Espaco` marcado como indisponivel
- Nao permitir `Horarios` invalido, quando `fim` for anterior ou igual a `inicio`
- Nao permitir reserva em data ou horario passados
- Toda alteracao relevante deve poder gerar uma `Notificacao`

## Regras por perfil de usuario

### `Solicitante`

- Pode consultar espacos e criar reservas
- Pode cancelar apenas as proprias reservas
- Deve receber notificacoes sobre criacao, alteracao ou cancelamento das suas reservas

### `Admin`

- Pode cadastrar e atualizar espacos
- Pode associar espacos a um predio
- Pode marcar indisponibilidade
- Pode cancelar qualquer reserva
- Pode consultar notificacoes administrativas quando houver

## Regras por estrutura do dominio

### `Espaco`

- Todo espaco deve pertencer a um `Predio`
- Todo espaco deve possuir tipo concreto: `Sala`, `Auditorio`, `Quadra` ou `Laboratorio`
- A verificacao de disponibilidade deve considerar reservas existentes no mesmo intervalo

### `Reserva`

- Deve estar ligada a um `Solicitante`
- Deve apontar para exatamente um `Espaco`
- Deve conter um objeto `Horarios` valido

### `Notificacao`

- Pode ser emitida para confirmar criacao de reserva
- Pode ser emitida para avisar cancelamento
- Pode ser emitida para comunicar mudanca de disponibilidade do espaco

## Casos invalidos esperados

- Tentativa de reservar um espaco ja reservado no mesmo periodo
- Tentativa de reservar um espaco indisponivel
- Tentativa de reservar com intervalo de tempo inconsistente
- Tentativa de reservar no passado
- Tentativa de cancelar reserva de outro usuario sem permissao administrativa

## Comportamento esperado da aplicacao

- Rejeitar requisicoes invalidas com erro de negocio claro
- Preservar consistencia entre disponibilidade do espaco e reservas confirmadas
- Registrar a reserva e seus eventos de forma rastreavel
- Manter notificacoes coerentes com as acoes relevantes do sistema
