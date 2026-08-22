# Backend Integration Test

Load only when the READY Strategy selects `backend-it`.

## Boundary

Use backend integration assets, runtime, and command declared in
`Tests/test-index.yml` and `Tests/README.md`. This layer proves behavior across
the application and declared infrastructure substitutes. It does not prove a
browser journey or production topology.

## Procedure

1. Read the Execution Plan row, target service/controller/repository, indexed
   integration tests, runtime requirements, and runner.
2. Confirm the required container or service runtime is available before
   changing tests. Read `../runtimes/colima.md` only when a macOS Colima
   runtime must be prepared or diagnosed.
3. Keep the infrastructure named by the Strategy real; do not mock persistence,
   transactions, locking, or side effects the execution item must prove.
4. Apply only the planned action, create deterministic fixtures, isolate state,
   and assert both the API/service observation and required persisted effects.
5. Run a focused integration selector when supported, then the exact Strategy
   command. Record image/runtime and cleanup behavior.

## Evidence

Record the Case ID, test Case ID, execution ID, test asset, runtime and image, fixture/reset state,
tables or side effects observed, command, result, and artifacts.
