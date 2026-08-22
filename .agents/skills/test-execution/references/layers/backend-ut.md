# Backend Unit Test

Load only when the READY Strategy selects `backend-ut`.

## Boundary

Use the project's backend unit framework and assets declared under
`Tests/test-index.yml`. This layer proves isolated rules, calculations,
validation, and state decisions. It does not prove database transactions,
framework wiring, external services, or browser behavior.

## Procedure

1. Read the Execution Plan row, target source, indexed unit tests, and runner.
2. Keep the test outside real infrastructure; control external collaborators,
   time, and generated identifiers.
3. Implement only the planned `run-existing`, `extend-existing`, `create-new`,
   or `repair-test` action while preserving the Case oracle.
4. Assert business inputs, outputs, errors, and state transitions rather than
   private methods or incidental interaction counts.
5. Run a focused project command when the runner supports one, then run the
   exact backend-unit command from the Strategy.

## Evidence

Record the Case ID, test Case ID, execution ID, test class or file, focused and layer commands,
runtime, result, and oracle observation. Do not claim persistence or HTTP
wiring evidence from a controlled unit test.
