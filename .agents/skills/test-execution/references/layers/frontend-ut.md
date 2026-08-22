# Frontend Unit Test

Load only when the READY Strategy selects `frontend-ut`.

## Boundary

Use the frontend unit framework and assets declared in `Tests/test-index.yml`.
This layer proves pure frontend module behavior. It does not prove rendered
page wiring, real HTTP, backend rules, or browser journeys.

## Procedure

1. Read the Execution Plan row, target module, indexed tests, package scripts,
   and project runner.
2. Keep the test deterministic and independent of a real browser, network, or
   backend. Control storage and time when the case requires them.
3. Apply only the planned action and preserve the Case inputs and oracle.
4. Assert returned values, state mappings, formatting, validation, or payload
   construction rather than framework internals.
5. Run the narrowest matching test selector, then the exact frontend-unit
   command from the Strategy.

## Evidence

Record the Case ID, test Case ID, execution ID, test file, focused and layer commands, runtime,
result, and oracle observation. Escalate page/client cooperation to Strategy
instead of silently broadening this layer.
