# Padrao de Commits e PRs

Este repositorio segue um padrao simples e consistente para branches, commits e pull requests.

## Branches

Use nomes curtos e descritivos, sempre com prefixo funcional:

- `feat/...` para novas funcionalidades
- `fix/...` para correcao de bugs
- `docs/...` para alteracoes de documentacao
- `chore/...` para ajustes de manutencao

Exemplos:

- `feat/reservation-user-profiles`
- `fix/notificacao-request-flow`
- `docs/update-readme-flow`

Nao use nomes com referencia a IA, assistente ou ferramentas no nome da branch.

## Commits

Os commits devem seguir o estilo convencional:

```text
tipo(escopo): descricao curta
```

Tipos mais usados neste projeto:

- `feat`
- `fix`
- `docs`
- `chore`

Escopos comuns:

- `model`
- `repository`
- `service`
- `controller`
- `dto`
- `config`
- `api`
- `postman`

Exemplos reais do repositorio:

- `feat(model): add usuario model`
- `feat(repository): add reserva repository`
- `feat(service): add solicitante service`
- `feat(controller): add solicitante controller`
- `feat(dto): add create reserva request`
- `feat(config): add default admin bootstrap`
- `docs(api): update endpoints for reservation flow`
- `docs(postman): update collection for reservation flow`
- `fix(model): add explicit accessors for runtime`
- `chore(dto): remove placeholder file`

## Regra de granularidade

Quando a entrega mexer em varios arquivos independentes, prefira commits pequenos e organizados.

Padrao esperado:

- um commit por arquivo novo ou por unidade logica pequena
- separar mudancas de `dto`, `service`, `controller`, `docs` e `config`
- evitar commits grandes misturando varias responsabilidades

Exemplo de sequencia esperada:

1. `feat(dto): add create reserva request`
2. `feat(service): update reserva creation flow`
3. `feat(controller): update reserva request flow`
4. `docs(api): update endpoints for reservation flow`

## Pull Requests

Os PRs devem ser abertos a partir de uma branch dedicada e com foco em uma entrega clara.

Boas praticas:

- abrir um PR por funcionalidade ou correcao
- usar titulo curto e direto em ingles simples
- descrever o que mudou, por que mudou e o impacto
- informar como a mudanca foi validada
- manter o PR pequeno o suficiente para revisao

Formato sugerido para descricao:

```md
## What changed
- ...

## Why it changed
- ...

## Impact
- ...

## Validation
- ...
```

Exemplos de titulos de PR:

- `adjust user profiles for reservation flow`
- `fix model accessors for runtime`
- `add controller layer`

## Observacoes

- Quando houver dependencia entre camadas, prefira empilhar PRs pequenos e coerentes.
- Se a mudanca for apenas documental, use `docs(...)`.
- Se remover arquivo placeholder, use `chore(...)`.
