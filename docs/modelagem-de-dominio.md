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
    subgraph Pessoas["Pessoas"]
        Usuario[Usuario<br/>ADMIN ou SOLICITANTE]
    end

    subgraph Localizacao["Espacos fisicos"]
        Predio[Predio<br/>bloco fisico]
        Espaco[Espaco<br/>sala, auditorio,<br/>quadra ou laboratorio]
        RecursoEspaco[RecursoEspaco<br/>equipamento/facilidade]
    end

    subgraph Agenda["Agenda"]
        HorarioFuncionamento[HorarioFuncionamento<br/>janela recorrente]
        Reserva[Reserva<br/>agendamento]
        HorarioReserva[HorarioReserva<br/>inicio e fim]
        Indisponibilidade[Indisponibilidade<br/>bloqueio planejado]
    end

    subgraph Apoio["Apoio operacional"]
        Notificacao[Notificacao<br/>aviso ao usuario]
        PoliticaReserva[PoliticaReserva<br/>parametros globais]
    end

    Usuario -->|1:N cria/cancela| Reserva
    Usuario -->|1:N recebe| Notificacao
    Usuario -->|1:N registra| Indisponibilidade
    Predio -->|1:N agrupa| Espaco
    Predio -->|1:N abertura geral| HorarioFuncionamento
    Espaco -->|1:N horario proprio| HorarioFuncionamento
    Espaco -->|1:N recebe| Reserva
    Espaco -->|1:N bloqueios| Indisponibilidade
    Espaco <-->|N:N recursos| RecursoEspaco
    Reserva -->|1:1 periodo| HorarioReserva
    Reserva -->|1:N contexto| Notificacao
    PoliticaReserva -.->|1:N validacoes| Reserva

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

    Predio "1" --> "N" Espaco : 1:N contem
    Predio "1" --> "N" HorarioFuncionamento : 1:N horario geral
    Espaco "1" --> "N" HorarioFuncionamento : 1:N horario proprio
    Espaco "1" --> "N" Indisponibilidade : 1:N bloqueios
    Usuario "1" --> "N" Indisponibilidade : 1:N criadaPor
    Espaco "N" --> "N" RecursoEspaco : N:N recursos
    Usuario "1" --> "N" Reserva : 1:N solicitante
    Espaco "1" --> "N" Reserva : 1:N agenda
    Reserva "1" *-- "1" HorarioReserva : 1:1 embute
    Usuario "1" --> "N" Notificacao : 1:N destinatario
    Reserva "1" --> "N" Notificacao : 1:N contexto
```

### Visao relacional JPA

Esta leitura organiza as tabelas por responsabilidade e reduz cruzamento de linhas. `HorarioReserva` nao aparece como tabela porque e um objeto embutido em `Reserva`.

```mermaid
flowchart LR
    subgraph Pessoas["Pessoas"]
        USUARIO["USUARIO<br/>PK id<br/>UK email<br/>nome, senha_hash<br/>papel, tipo_solicitante"]
    end

    subgraph Espacos["Espacos fisicos"]
        PREDIO["PREDIO<br/>PK id<br/>nome, codigo<br/>localizacao"]
        ESPACO["ESPACO<br/>PK id<br/>FK predio_id<br/>nome, tipo, capacidade<br/>indisponivel, motivo"]
        RECURSO_ESPACO["RECURSO_ESPACO<br/>PK id<br/>UK nome<br/>descricao"]
        ESPACO_RECURSO["ESPACO_RECURSO<br/>FK espaco_id<br/>FK recurso_id"]
    end

    subgraph Agenda["Agenda e disponibilidade"]
        HORARIO_FUNCIONAMENTO["HORARIO_FUNCIONAMENTO<br/>PK id<br/>FK predio_id opcional<br/>FK espaco_id opcional<br/>dia_semana, abertura<br/>fechamento, ativo"]
        RESERVA["RESERVA<br/>PK id<br/>FK solicitante_id<br/>FK espaco_id<br/>inicio, fim<br/>motivo, cancelada, criada_em"]
        INDISPONIBILIDADE["INDISPONIBILIDADE<br/>PK id<br/>FK espaco_id<br/>FK criada_por_id<br/>inicio, fim, motivo"]
    end

    subgraph Comunicacao["Comunicacao"]
        NOTIFICACAO["NOTIFICACAO<br/>PK id<br/>FK destinatario_id<br/>FK reserva_id opcional<br/>mensagem, lida, enviada_em"]
    end

    PREDIO -->|1:N| ESPACO
    PREDIO -->|1:N| HORARIO_FUNCIONAMENTO
    ESPACO -->|1:N| HORARIO_FUNCIONAMENTO

    ESPACO -->|1:N| RESERVA
    USUARIO -->|1:N solicitante| RESERVA
    RESERVA -->|1:N| NOTIFICACAO
    USUARIO -->|1:N destinatario| NOTIFICACAO

    ESPACO -->|1:N| INDISPONIBILIDADE
    USUARIO -->|1:N criadaPor| INDISPONIBILIDADE

    ESPACO -->|1:N| ESPACO_RECURSO
    RECURSO_ESPACO -->|1:N| ESPACO_RECURSO

    classDef table fill:#ffffff,stroke:#495057,color:#102a43
    classDef join fill:#f1f3f5,stroke:#868e96,color:#102a43
    classDef people fill:#e7f5ff,stroke:#1c7ed6,color:#102a43
    classDef place fill:#e6fcf5,stroke:#0ca678,color:#102a43
    classDef schedule fill:#fff4e6,stroke:#f08c00,color:#102a43
    classDef comm fill:#f8f0fc,stroke:#9c36b5,color:#102a43

    class USUARIO people
    class PREDIO,ESPACO,RECURSO_ESPACO place
    class ESPACO_RECURSO join
    class HORARIO_FUNCIONAMENTO,RESERVA,INDISPONIBILIDADE schedule
    class NOTIFICACAO comm
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
