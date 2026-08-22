# E2E Smoke Test

Load only when the READY Strategy selects `e2e`.

## Boundary

Use browser tests, fixtures, services, and commands declared in
`Tests/test-index.yml` and `Tests/README.md`. This layer proves a critical
user-visible journey in the declared environment. It does not prove exhaustive
business-rule combinations or an undeclared deployed topology.

## Procedure

1. Read the Execution Plan row, indexed browser tests and fixtures, application
   startup contract, and runner.
2. Confirm prerequisite Strategy items are green or explicitly waived.
3. Apply only the planned action. Use stable role/label selectors, deterministic
   accounts or seeded data, and observable user outcomes.
4. Keep the path small; do not duplicate matrices that lower layers already
   prove. Label fixture-server evidence as local-runtime evidence.
5. Run the focused browser spec when supported, then the Strategy command.
   Capture trace, screenshot, or video on failure when configured.

## Evidence

Record the Case ID, test Case ID, execution ID, browser path, environment, visible oracle observation,
command, result, and artifact paths. Natural-language UI exploration may inform
a script but is not deterministic passing evidence.
