# Modelagem de Dominio

## Visao geral

O `Classroom Scheduler` foi modelado para resolver agendamento de espacos com 10 classes de dominio que carregam responsabilidades reais. A decisao atual evita subclasses vazias: `Admin` e `Solicitante` sao papeis de `Usuario`, enquanto `Sala`, `Auditorio`, `Quadra` e `Laboratorio` sao tipos de `Espaco`.

Enums de apoio:

- `PapelUsuario`: `ADMIN` ou `SOLICITANTE`
- `TipoSolicitante`: `ALUNO` ou `FUNCIONARIO`
- `TipoEspaco`: `SALA`, `AUDITORIO`, `QUADRA` ou `LABORATORIO`
- `DiaSemana`: dias usados em horario de funcionamento

## As 10 classes de dominio

### `Usuario`

Representa qualquer pessoa autenticada no sistema.

- Mantem `nome`, `email`, `senhaHash`, `papel` e `tipoSolicitante`
- Define permissao por `papel`, sem precisar de classe `Admin` ou `Solicitante`
- Para solicitantes, o tipo e inferido pelo dominio do email institucional

### `Predio`

Representa o bloco ou edificio fisico.

- Agrupa espacos por localizacao
- Possui horarios de funcionamento proprios
- Permite filtros e organizacao operacional

### `Espaco`

Representa qualquer recurso fisico reservavel.

- Usa `tipo` para identificar sala, auditorio, quadra ou laboratorio
- Mantem capacidade, disponibilidade e motivo de indisponibilidade
- Pertence a um predio
- Pode possuir recursos, horarios especificos e indisponibilidades

### `HorarioFuncionamento`

Representa dias e janelas de abertura.

- Pode estar ligado a um predio ou a um espaco
- Usa `DiaSemana`, `abertura`, `fechamento` e `ativo`
- Permite diferenciar funcionamento geral do predio e excecoes por espaco

### `Reserva`

Representa o agendamento feito por um usuario.

- Liga `Usuario` e `Espaco`
- Guarda motivo, cancelamento e data de criacao
- Compoe `HorarioReserva` para validar inicio/fim

### `HorarioReserva`

Objeto embutido em `Reserva`.

- Encapsula `inicio` e `fim`
- Valida que `fim` e posterior ao `inicio`
- Oferece logica de conflito temporal

### `Indisponibilidade`

Representa bloqueios planejados de um espaco.

- Guarda intervalo, motivo e usuario que criou o bloqueio
- E adequada para manutencao, eventos internos ou restricoes temporarias
- Complementa o booleano operacional `indisponivel` de `Espaco`

### `RecursoEspaco`

Representa equipamentos ou facilidades disponiveis no espaco.

- Exemplos: projetor, quadro branco, sistema de som
- Relaciona-se com varios espacos
- Permite filtros futuros por infraestrutura

### `Notificacao`

Representa avisos emitidos aos usuarios.

- Guarda destinatario, reserva opcional, mensagem, leitura e envio
- E usada para eventos de reserva e avisos operacionais

### `PoliticaReserva`

Representa parametros de regra de reserva.

- Define antecedencia minima
- Define duracao maxima
- Indica se fim de semana e permitido
- Indica se aprovacao administrativa e exigida

## Relacoes principais

- `Predio` possui muitos `Espaco`
- `Predio` pode possuir varios `HorarioFuncionamento`
- `Espaco` pode possuir horarios especificos, indisponibilidades e recursos
- `Usuario` cria reservas e recebe notificacoes
- `Reserva` associa um usuario a um espaco e contem um `HorarioReserva`
- `Notificacao` referencia um usuario destinatario e pode referenciar uma reserva

## Visualizacoes do dominio

### Mapa rapido

Esta visao mostra o que existe no dominio e como cada parte participa do agendamento.

```mermaid
flowchart LR
    Usuario[Usuario<br/>ADMIN ou SOLICITANTE]
    Predio[Predio<br/>bloco fisico]
    Espaco[Espaco<br/>sala, auditorio,<br/>quadra ou laboratorio]
    HorarioFuncionamento[HorarioFuncionamento<br/>janela recorrente]
    Reserva[Reserva<br/>agendamento]
    HorarioReserva[HorarioReserva<br/>inicio e fim]
    Indisponibilidade[Indisponibilidade<br/>bloqueio planejado]
    RecursoEspaco[RecursoEspaco<br/>equipamento/facilidade]
    Notificacao[Notificacao<br/>aviso ao usuario]
    PoliticaReserva[PoliticaReserva<br/>parametros globais]

    Usuario -->|cria/cancela| Reserva
    Usuario -->|recebe| Notificacao
    Usuario -->|registra bloqueio| Indisponibilidade

    Predio -->|agrupa| Espaco
    Predio -->|define abertura geral| HorarioFuncionamento

    Espaco -->|recebe| Reserva
    Espaco -->|pode sobrescrever horarios| HorarioFuncionamento
    Espaco -->|pode estar bloqueado por| Indisponibilidade
    Espaco <-->|oferece| RecursoEspaco

    Reserva -->|embute| HorarioReserva
    Reserva -->|gera contexto para| Notificacao
    PoliticaReserva -.->|orienta validacoes| Reserva

    classDef actor fill:#e7f5ff,stroke:#1c7ed6,color:#102a43
    classDef place fill:#e6fcf5,stroke:#0ca678,color:#102a43
    classDef schedule fill:#fff4e6,stroke:#f08c00,color:#102a43
    classDef support fill:#f8f0fc,stroke:#9c36b5,color:#102a43

    class Usuario actor
    class Predio,Espaco,RecursoEspaco place
    class Reserva,HorarioReserva,HorarioFuncionamento,Indisponibilidade schedule
    class Notificacao,PoliticaReserva support
```

### Diagrama de classes

```mermaid
classDiagram
    direction LR

    class Usuario {
        <<Entity>>
        +Long id
        +String nome
        +String email
        -String senhaHash
        +PapelUsuario papel
        +TipoSolicitante tipoSolicitante
    }

    class Predio {
        <<Entity>>
        +Long id
        +String nome
        +String codigo
        +String localizacao
    }

    class Espaco {
        <<Entity>>
        +Long id
        +String nome
        +TipoEspaco tipo
        +Integer capacidade
        +boolean indisponivel
        +String motivoIndisponibilidade
    }

    class HorarioFuncionamento {
        <<Entity>>
        +Long id
        +DiaSemana diaSemana
        +LocalTime abertura
        +LocalTime fechamento
        +boolean ativo
    }

    class Reserva {
        <<Entity>>
        +Long id
        +String motivo
        +boolean cancelada
        +LocalDateTime criadaEm
        +prePersist()
        +preUpdate()
    }

    class HorarioReserva {
        <<Value Object>>
        +LocalDateTime inicio
        +LocalDateTime fim
        +validar()
        +conflita(HorarioReserva outro) boolean
    }

    class Indisponibilidade {
        <<Entity>>
        +Long id
        +LocalDateTime inicio
        +LocalDateTime fim
        +String motivo
    }

    class RecursoEspaco {
        <<Entity>>
        +Long id
        +String nome
        +String descricao
    }

    class Notificacao {
        <<Entity>>
        +Long id
        +String mensagem
        +boolean lida
        +LocalDateTime enviadaEm
        +prePersist()
    }

    class PoliticaReserva {
        <<Entity>>
        +Long id
        +String nome
        +Integer antecedenciaMinimaHoras
        +Integer duracaoMaximaHoras
        +boolean permiteFimDeSemana
        +boolean requerAprovacaoAdmin
    }

    Predio "1" --> "0..*" Espaco : contem
    Predio "0..1" --> "0..*" HorarioFuncionamento : horario geral
    Espaco "0..1" --> "0..*" HorarioFuncionamento : excecao/horario proprio
    Espaco "1" --> "0..*" Indisponibilidade : bloqueios
    Usuario "0..1" --> "0..*" Indisponibilidade : criadaPor
    Espaco "0..*" --> "0..*" RecursoEspaco : recursos
    Usuario "1" --> "0..*" Reserva : solicitante
    Espaco "1" --> "0..*" Reserva : agenda
    Reserva "1" *-- "1" HorarioReserva : embute
    Usuario "1" --> "0..*" Notificacao : destinatario
    Reserva "0..1" --> "0..*" Notificacao : contexto
```

### Visao relacional JPA

Esta leitura ajuda a enxergar quais tabelas e chaves aparecem no banco H2. `HorarioReserva` nao aparece como tabela porque e um objeto embutido em `Reserva`.

```mermaid
erDiagram
    USUARIO ||--o{ RESERVA : solicita
    USUARIO ||--o{ NOTIFICACAO : recebe
    USUARIO ||--o{ INDISPONIBILIDADE : cria

    PREDIO ||--o{ ESPACO : contem
    PREDIO ||--o{ HORARIO_FUNCIONAMENTO : possui
    ESPACO ||--o{ HORARIO_FUNCIONAMENTO : possui
    ESPACO ||--o{ INDISPONIBILIDADE : bloqueia
    ESPACO ||--o{ RESERVA : recebe
    ESPACO ||--o{ ESPACO_RECURSO : vincula
    RECURSO_ESPACO ||--o{ ESPACO_RECURSO : vincula
    RESERVA ||--o{ NOTIFICACAO : origina

    USUARIO {
        bigint id PK
        string nome
        string email UK
        string senha_hash
        string papel
        string tipo_solicitante
    }

    PREDIO {
        bigint id PK
        string nome
        string codigo
        string localizacao
    }

    ESPACO {
        bigint id PK
        bigint predio_id FK
        string nome
        string tipo
        int capacidade
        boolean indisponivel
        string motivo_indisponibilidade
    }

    HORARIO_FUNCIONAMENTO {
        bigint id PK
        bigint predio_id FK
        bigint espaco_id FK
        string dia_semana
        time abertura
        time fechamento
        boolean ativo
    }

    RESERVA {
        bigint id PK
        bigint solicitante_id FK
        bigint espaco_id FK
        datetime inicio
        datetime fim
        string motivo
        boolean cancelada
        datetime criada_em
    }

    INDISPONIBILIDADE {
        bigint id PK
        bigint espaco_id FK
        bigint criada_por_id FK
        datetime inicio
        datetime fim
        string motivo
    }

    RECURSO_ESPACO {
        bigint id PK
        string nome UK
        string descricao
    }

    ESPACO_RECURSO {
        bigint espaco_id FK
        bigint recurso_id FK
    }

    NOTIFICACAO {
        bigint id PK
        bigint destinatario_id FK
        bigint reserva_id FK
        string mensagem
        boolean lida
        datetime enviada_em
    }
```

### Fluxo principal de reserva

```mermaid
sequenceDiagram
    autonumber
    actor Solicitante
    participant API as ReservaController
    participant Service as ReservaService
    participant Espacos as EspacoRepository
    participant Reservas as ReservaRepository
    participant Notificacoes as NotificacaoService

    Solicitante->>API: POST /reservas
    API->>Service: criarReserva(request, usuarioAtual)
    Service->>Espacos: buscar espaco
    Espacos-->>Service: Espaco
    Service->>Service: validar horario, disponibilidade e permissao
    Service->>Reservas: buscar conflitos do mesmo espaco
    Reservas-->>Service: reservas existentes

    alt existe conflito ou regra invalida
        Service-->>API: erro de regra de negocio
        API-->>Solicitante: 400 Bad Request
    else reserva valida
        Service->>Reservas: salvar reserva
        Reservas-->>Service: Reserva criada
        Service->>Notificacoes: emitir aviso
        Service-->>API: Reserva criada
        API-->>Solicitante: 201 Created
    end
```

## Decisoes de design

- `Usuario` concreto reduz tabelas e classes sem comportamento proprio.
- `Espaco` concreto com `TipoEspaco` evita subclasses sem atributos diferentes.
- `HorarioFuncionamento` separa disponibilidade recorrente de reservas pontuais.
- `Indisponibilidade` permite registrar bloqueios planejados sem misturar com reserva.
- `HorarioReserva` continua como objeto de valor embutido, concentrando validacao temporal.
- `PoliticaReserva` deixa regras parametrizaveis prontas para evolucao sem espalhar constantes.
