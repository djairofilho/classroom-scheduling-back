# Backlog do Produto

## Objetivo

Este documento organiza as proximas funcionalidades e melhorias do `Classroom Scheduler` antes de transformá-las em itens do GitHub Project. A ideia é deixar claro o que ja existe, o que falta no fluxo principal e quais evolucoes valem entrar no backlog oficial.

## Funcionalidades ja disponiveis

- Cadastro e consulta de `Predio`
- Cadastro e consulta de `Espaco`
- Controle de indisponibilidade de espacos
- Cadastro e consulta de `Solicitante`
- Criacao, consulta e cancelamento de `Reserva`
- Consulta e marcacao de leitura de `Notificacao`
- `Admin` padrao via configuracao de ambiente

## Backlog priorizado

### 1. Login com JWT

**Objetivo**

Permitir autenticacao real na API e separar claramente as permissoes de `Admin` e `Solicitante`.

**Entregas esperadas**

- Endpoint de login
- Geração de token JWT
- Filtro de autenticacao nas rotas protegidas
- Diferenciacao de acesso por perfil
- Configuracao de expiracao e segredo do token por ambiente

**Impacto no dominio**

- `Usuario` passa a ter papel autentificavel
- `Admin` e `Solicitante` deixam de depender apenas de convencao de uso da API

**Sugestao de card no Project**

- `Implementar autenticacao com JWT`

### 2. Admin aprovar pedido de reserva

**Objetivo**

Transformar a reserva em um fluxo de solicitacao e aprovacao, permitindo que o `Admin` aprove ou recuse reservas.

**Entregas esperadas**

- Novo status na reserva, como `PENDENTE`, `APROVADA`, `RECUSADA`, `CANCELADA`
- Endpoint para o `Admin` atualizar o status da reserva
- Regra para impedir uso da reserva antes de aprovacao
- Notificacao automatica ao solicitante quando a reserva for aprovada ou recusada

**Impacto no dominio**

- `Reserva` deixa de ser apenas criada/cancelada e passa a ter ciclo de vida mais completo

**Sugestao de card no Project**

- `Adicionar aprovacao administrativa de reservas`

### 3. Classe `Evento` para organizar agendamentos

**Objetivo**

Criar uma camada de organizacao acima de `Reserva`, permitindo representar eventos e casos recorrentes como aulas.

**Direcao proposta**

- Introduzir uma classe base `Evento`
- `Aula` como subclasse de `Evento`
- `Aula` deve suportar recorrencia
- Eventos podem gerar uma ou varias reservas relacionadas

**Possiveis subclasses futuras**

- `Aula`
- `Reuniao`
- `Treino`
- `Palestra`

**Impacto no dominio**

- O agendamento deixa de ser puramente pontual
- Passa a existir organizacao por contexto academico, e nao apenas por slot de horario

**Sugestao de card no Project**

- `Criar entidade Evento e suporte a aulas recorrentes`

### 4. Melhorar validacoes do fluxo atual

**Objetivo**

Fortalecer as regras atuais para reduzir erros de uso e inconsistencias de dados.

**Melhorias sugeridas**

- Validar email unico para todos os perfis
- Validar capacidade minima e nome obrigatorio em `Espaco`
- Validar que `Predio` nao tenha codigo duplicado
- Validar `Reserva` no passado e conflitos de horario de forma centralizada
- Validar mensagem obrigatoria em `Notificacao`
- Padronizar respostas de erro com mensagens mais previsiveis

**Sugestao de card no Project**

- `Reforcar validacoes de entrada e regras de negocio`

### 5. Refatoracao com Lombok

**Objetivo**

Retomar o uso consistente de Lombok de forma segura, reduzindo boilerplate sem quebrar o build.

**Motivacao**

Alguns models receberam getters e setters explicitos por problemas de compilacao anteriores. Vale revisar o uso de Lombok com calma, junto da configuracao do Maven e da JDK usada no projeto.

**Escopo sugerido**

- Revisar configuracao de annotation processing
- Padronizar uso de `@Getter`, `@Setter`, `@NoArgsConstructor`
- Garantir compilacao estavel no Maven antes de remover codigo explicito

**Sugestao de card no Project**

- `Refatorar models para uso consistente de Lombok`

## Melhorias simples e incrementais

Estas sugestoes sao menores e podem virar cards independentes ou subtarefas:

- Criar DTOs de resposta para desacoplar a API das entidades JPA
- Paginar listagens de reservas, espacos e notificacoes
- Adicionar seed de dados para demonstracao local
- Gerar notificacoes automaticamente ao criar ou cancelar reserva
- Criar endpoints de aprovacao e recusa com motivo administrativo
- Padronizar exemplos da collection Postman com fluxo completo
- Adicionar testes de controller e service para os fluxos principais

## Sugestao de organizacao no GitHub Project

### Coluna ou fase inicial

- `Backlog`
- `Ready`
- `In Progress`
- `Review`
- `Done`

### Cards iniciais recomendados

- `Implementar autenticacao com JWT`
- `Adicionar aprovacao administrativa de reservas`
- `Criar entidade Evento e suporte a aulas recorrentes`
- `Reforcar validacoes de entrada e regras de negocio`
- `Refatorar models para uso consistente de Lombok`
- `Criar DTOs de resposta para a API`
- `Automatizar notificacoes em reservas`

## Ordem recomendada

1. Login com JWT
2. Aprovacao administrativa de reserva
3. Validacoes do fluxo atual
4. Entidade `Evento` com `Aula` recorrente
5. Refatoracao com Lombok
