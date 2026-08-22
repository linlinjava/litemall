# Contract Test

Load only when the READY Strategy selects `contract`.

## Boundary

Use contract assets and tests declared in `Tests/test-index.yml`. This layer
proves compatibility at a declared producer/consumer or external boundary. It
does not prove complete business behavior, persistence, or browser wiring.

## Procedure

1. Read the Execution Plan row, authoritative contract, producer and consumer
   surfaces, indexed fixtures/tests, and runner.
2. Identify the exact compatibility claim: route, method, required field,
   type, schema, event, XML, or serialization rule.
3. Apply only the planned action. Do not weaken required fields or validate a
   self-authored fixture without connecting it to the declared boundary.
4. Record intentional breaking changes and affected consumers instead of
   silently updating both sides.
5. Run the focused contract selector when available, then the Strategy command.

## Evidence

Record the Case ID, test Case ID, execution ID, contract and test assets, compatibility decision,
command, result, and validated boundary. Persistence and side effects require
a separate Strategy execution item.
