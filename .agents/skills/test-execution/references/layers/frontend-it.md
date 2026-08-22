# Frontend Integration Test

Load only when the READY Strategy selects `frontend-it`.

## Boundary

Use frontend integration assets and package scripts declared in
`Tests/test-index.yml`. This layer proves cooperation among frontend modules
and request construction against a controlled network boundary. It does not
prove server-side rules, persistence, or deployed browser behavior.

## Procedure

1. Read the Execution Plan row, page/client/store/router surfaces, indexed
   tests, request-spy or mock-server support, and runner.
2. Keep internal frontend modules real; control the network only at the
   declared boundary.
3. Apply only the planned action and preserve the Case action, data, and oracle.
4. Assert user-visible state and captured request path, query, body, or error
   behavior required by the case.
5. Reset handlers and shared state per test, run a focused selector when
   available, then run the Strategy command.

## Evidence

Record the Case ID, test Case ID, execution ID, test file, controlled boundary, request or visible
observation, command, runtime, and result. Do not report controlled responses
as backend correctness.
