# Test Layer Capabilities

Use this reference from `test-strategy` to select the lowest-cost evidence
layer that can prove each approved business case. Confirm available layers,
assets, commands, and runtimes in the project's `Tests/test-index.yml`.

| Layer | Use when the oracle requires | Proves | Does not prove | Typical runtime |
| --- | --- | --- | --- | --- |
| Backend Unit | isolated backend rules, calculations, validation, or state decisions | deterministic behavior with controlled collaborators | persistence, transactions, deployed wiring, browser behavior | language test runner |
| Frontend Unit | pure presentation, formatting, state mapping, or payload helper logic | deterministic frontend module behavior | rendered page wiring, real HTTP, backend rules | Node test runner |
| Contract | route, method, request/response schema, event, or serialization compatibility | producer/consumer boundary compatibility | complete business behavior or persistence | schema or contract runner |
| Frontend Integration | page/client/store/router cooperation with a controlled network boundary | request construction and frontend states across modules | server rules or deployed-system behavior | DOM runtime and request spy/mock server |
| Backend Integration | database, transaction, cache, queue, filesystem, or framework wiring | real integration behavior inside the declared runtime | browser journey or production topology | service runtime and containers when required |
| E2E/System | a critical user journey through real application boundaries | browser-visible workflow in the declared environment | exhaustive rule combinations or undeclared deployment properties | browser and application services |

## Selection Rules

- Select layers after Case Design approval; do not push layer choices back into
  business case identity.
- Add a second layer only for a distinct evidence claim, not for pyramid quotas.
- Prefer focused deterministic evidence before infrastructure or browser cost.
- Treat API endpoint behavior and consumer/provider compatibility as different
  claims; do not hide them under a generic integration label.
- Record what the chosen layer cannot prove as residual risk or another
  execution item.
