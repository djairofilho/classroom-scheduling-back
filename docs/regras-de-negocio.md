# Regras de Negocio

## Objetivo

As regras do `Classroom Scheduler` garantem que o sistema evite conflitos, respeite restricoes de uso e mantenha a reserva de salas previsivel e segura.

## Regras obrigatorias

- Nao permitir reserva de uma sala ja ocupada no mesmo intervalo
- Nao permitir reserva de sala com manutencao ativa no horario solicitado
- Nao permitir horario invalido, quando `fim` for anterior ou igual a `inicio`
- Nao permitir reserva em data ou horario passados

## Regras por perfil de usuario

### Aluno

- Pode consultar salas e criar reservas
- Pode cancelar apenas as proprias reservas
- Possui limite maximo de duracao por reserva

### Professor

- Pode consultar salas e criar reservas
- Pode cancelar as proprias reservas
- Possui maior flexibilidade de duracao, conforme politica definida

### Administrador

- Pode criar e atualizar salas
- Pode associar recursos e capacidade
- Pode bloquear salas para manutencao
- Pode cancelar qualquer reserva

## Papel da `PoliticaReserva`

A classe `PoliticaReserva` concentra as validacoes centrais para que as regras nao fiquem dispersas entre controller, service e entidades.

Exemplos de responsabilidades:

- validar se o usuario pode reservar naquele contexto
- validar duracao maxima para aluno
- validar janelas de reserva permitidas
- apoiar decisoes de cancelamento

Exemplo conceitual:

```java
class PoliticaReserva {

    boolean podeReservar(Usuario usuario, Horario horario) {
        return true;
    }

    boolean validarDuracao(Usuario usuario, Horario horario) {
        return true;
    }
}
```

## Casos invalidos esperados

- Tentativa de reservar uma sala ja reservada no mesmo periodo
- Tentativa de reservar uma sala bloqueada por manutencao
- Tentativa de reservar com intervalo de tempo inconsistente
- Tentativa de reservar no passado
- Tentativa de aluno exceder o limite de duracao
- Tentativa de cancelar reserva de outro usuario sem permissao administrativa

## Comportamento esperado da aplicacao

- Rejeitar requisicoes invalidas com erro de negocio claro
- Preservar consistencia entre disponibilidade da sala e reservas confirmadas
- Registrar o status da reserva para diferenciar reservas ativas e canceladas
