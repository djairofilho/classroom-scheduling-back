# Modelagem de Dominio

## Visao geral

O `Classroom Scheduler` foi modelado para resolver o agendamento de salas com um conjunto enxuto de classes, mas com boas decisoes de orientacao a objetos. O foco esta em representar corretamente usuarios, salas, reservas e regras de negocio.

## Classes e enums

### Usuarios

- `Usuario`: classe base com dados comuns e identificacao do tipo de usuario
- `Aluno`: especializacao com restricoes mais fortes de reserva
- `Professor`: especializacao com maior flexibilidade
- `Administrador`: especializacao com privilegios de gestao e cancelamento global

### Estrutura fisica

- `Sala`: representa a sala reservavel, com capacidade, predio e recursos
- `Predio`: representa a localizacao fisica da sala
- `Recurso`: representa itens disponiveis na sala, como projetor ou laboratorio

### Reserva

- `Reserva`: entidade principal de agendamento
- `Horario`: objeto de valor com `inicio` e `fim`, responsavel por validacao temporal e deteccao de conflito

### Controle e regras

- `Manutencao`: representa bloqueios temporarios da sala
- `PoliticaReserva`: centraliza regras de negocio de reserva
- `StatusReserva`: enum com estados da reserva
- `TipoUsuario`: enum para classificacao do perfil do usuario

## Relacoes principais

- `Aluno`, `Professor` e `Administrador` herdam de `Usuario`
- `Reserva` associa um `Usuario` a uma `Sala`
- `Reserva` contem um `Horario`
- `Sala` pertence a um `Predio`
- `Sala` possui uma lista de `Recurso`
- `Sala` pode possuir periodos de `Manutencao`

## Responsabilidades por elemento

### `Sala`

- Guardar dados da sala
- Informar capacidade e recursos disponiveis
- Servir como alvo de reservas e bloqueios

### `Reserva`

- Representar a reserva realizada por um usuario
- Manter status atual da reserva
- Permitir cancelamento conforme regra de negocio

### `Horario`

- Garantir que o intervalo seja valido
- Detectar conflitos com outro horario
- Encapsular a logica temporal sem espalhar comparacoes pela aplicacao

### `PoliticaReserva`

- Validar se o usuario pode reservar
- Verificar limite de duracao
- Concentrar regras sensiveis de negocio em um unico ponto

## Decisoes de design que fortalecem o projeto

- Uso de heranca para representar papeis com comportamento semelhante, mas permissoes diferentes
- Uso de objeto de valor para evitar logica de data espalhada
- Separacao de regras em `PoliticaReserva`, evitando uma classe de servico ou entidade excessivamente grande
- Uso de enums para explicitar estados e tipos aceitos pelo sistema

## Exemplo conceitual

```java
class Sala {
    Long id;
    String nome;
    int capacidade;
    Predio predio;
    List<Recurso> recursos;
}

class Reserva {
    Long id;
    Usuario usuario;
    Sala sala;
    Horario horario;
    StatusReserva status;
    String motivo;

    void cancelar() { }
}

class Horario {
    LocalDateTime inicio;
    LocalDateTime fim;

    boolean conflita(Horario outro) { return false; }
}
```
