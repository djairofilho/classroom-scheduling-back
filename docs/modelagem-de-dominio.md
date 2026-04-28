# Modelagem de Dominio

## Visao geral

O `Classroom Scheduler` foi modelado para resolver o agendamento de espacos com um conjunto obrigatorio de classes que deve guiar a implementacao do projeto. O foco esta em representar corretamente usuarios, espacos, reservas, horarios e notificacoes.

## Classes obrigatorias

### Usuarios

- `Usuario`: classe base com dados comuns de identificacao, autenticacao e contato
- `Admin`: especializacao de `Usuario` com poderes de gestao global
- `Solicitante`: especializacao de `Usuario` responsavel por criar e acompanhar reservas

### Estrutura fisica

- `Predio`: representa o bloco ou edificio ao qual os espacos pertencem
- `Espaco`: classe base para qualquer recurso fisico reservavel
- `Sala`: especializacao de `Espaco` para aulas e reunioes
- `Auditorio`: especializacao de `Espaco` para eventos e apresentacoes
- `Quadra`: especializacao de `Espaco` para atividades esportivas
- `Laboratorio`: especializacao de `Espaco` para aulas praticas e atividades com equipamentos

### Agendamento

- `Reserva`: entidade principal de agendamento
- `Horarios`: classe que encapsula `inicio` e `fim`, validando intervalo e conflito

### Comunicacao

- `Notificacao`: representa avisos emitidos pelo sistema sobre criacao, alteracao ou cancelamento de reservas

## Relacoes principais

- `Admin` e `Solicitante` herdam de `Usuario`
- `Sala`, `Auditorio`, `Quadra` e `Laboratorio` herdam de `Espaco`
- `Espaco` pertence a um `Predio`
- `Reserva` associa um `Solicitante` a um `Espaco`
- `Reserva` contem um `Horarios`
- `Notificacao` referencia um `Usuario` destinatario e pode estar associada a uma `Reserva`

## Responsabilidades por elemento

### `Usuario`

- Manter dados comuns como nome, email e identificacao
- Servir de base para permissao e rastreabilidade das acoes

### `Admin`

- Gerenciar espacos
- Intervir em reservas quando necessario
- Acompanhar operacoes administrativas do sistema

### `Solicitante`

- Solicitar reservas
- Cancelar as proprias reservas
- Consultar historico e notificacoes

### `Predio`

- Organizar a distribuicao fisica dos espacos
- Permitir filtros por localizacao

### `Espaco`

- Centralizar atributos comuns, como nome, capacidade e disponibilidade
- Servir como abstracao principal para o modulo de reservas

### `Sala`, `Auditorio`, `Quadra`, `Laboratorio`

- Especializar `Espaco` conforme o tipo de uso
- Permitir regras ou atributos especificos por categoria no futuro

### `Reserva`

- Representar a reserva realizada por um solicitante
- Manter status, motivo e vinculo com o espaco reservado
- Permitir cancelamento conforme regra de negocio

### `Horarios`

- Garantir que o intervalo seja valido
- Detectar conflitos com outro horario
- Encapsular a logica temporal sem espalhar comparacoes pela aplicacao

### `Notificacao`

- Informar eventos relevantes ao usuario
- Registrar leitura, data de envio e conteudo da mensagem

## Decisoes de design que fortalecem o projeto

- Uso de heranca em `Usuario` para separar papeis sem duplicar atributos comuns
- Uso de heranca em `Espaco` para permitir varios tipos de ambiente com a mesma regra central de reserva
- Uso de `Horarios` como objeto de valor do dominio
- Uso de `Notificacao` como parte explicita do modelo, e nao apenas detalhe tecnico de infraestrutura

## Exemplo conceitual

```java
abstract class Usuario {
    Long id;
    String nome;
    String email;
}

class Admin extends Usuario { }

class Solicitante extends Usuario { }

abstract class Espaco {
    Long id;
    String nome;
    int capacidade;
    Predio predio;
}

class Sala extends Espaco { }
class Auditorio extends Espaco { }
class Quadra extends Espaco { }
class Laboratorio extends Espaco { }

class Reserva {
    Long id;
    Solicitante solicitante;
    Espaco espaco;
    Horarios horarios;
    String motivo;
}

class Horarios {
    LocalDateTime inicio;
    LocalDateTime fim;

    boolean conflita(Horarios outro) { return false; }
}

class Notificacao {
    Long id;
    Usuario destinatario;
    Reserva reserva;
    String mensagem;
}
```
