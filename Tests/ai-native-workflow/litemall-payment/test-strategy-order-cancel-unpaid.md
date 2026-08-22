# Test Strategy：取消未支付订单

## Test Strategy

- `design_id`: `MALL-ORD-CANCEL-001-DESIGN`
- `review_id`: `MALL-ORD-CANCEL-001-REVIEW`
- `impacted_chains`: `order-lifecycle`, `stock-compensation`, `coupon-release`, `frontend-api-contract`
- `changed_surfaces`: order cancellation service/controller, inventory compensation, coupon release, mobile order API, payment-flow harness
- `affected_contracts`: `POST /order/cancel`, body `{ orderId }`
- `risks`: order status transition, duplicate cancellation, stock/coupon consistency, ownership guard
- `execution_profile`: `targeted-medium`
- `selected_cases`: `MALL-PAY-AC09-BE-UT-001`, `MALL-PAY-AC09-BE-UT-002`, `MALL-PAY-AC00-CONTRACT-001`, `MALL-PAY-AC09-FE-IT-001`, `MALL-PAY-AC09-BE-IT-001`, `MALL-PAY-AC09-E2E-001`
- `deferred_cases`: `TEST-ORD-CANCEL-AC06-001` — no failure-injection transaction test yet
- `required_runtimes`: Java/Maven, Node/npm, Docker-compatible runtime for Backend IT, Playwright Chromium
- `evidence_contract`: command, selected case ID, layer, runtime, result, report/trace path, evidence boundary, residual risk

## Execution Order

1. Script self-check and Backend UT — fast business rules and guards.
2. Contract — controller method/path and contract ID consistency.
3. Frontend IT — client request path and payload.
4. Backend IT — persistence primitives and MySQL evidence.
5. E2E — critical browser cancellation path through local harness.

## Commands

```bash
./Tests/scripts/run-layered-tests.sh backend-ut
./Tests/scripts/run-layered-tests.sh contract
./Tests/scripts/run-layered-tests.sh frontend-it
./Tests/scripts/run-layered-tests.sh backend-it
./Tests/scripts/run-layered-tests.sh e2e
```

## Gates

- Backend UT, Contract, Frontend IT, Backend IT, and E2E must execute intended
  tests; a zero-test or setup failure is `BLOCKED`, not green.
- Backend IT must record Docker/Testcontainers runtime and database reset.
- E2E must record local harness boundary and retain trace/screenshot on failure.
- Do not claim AC06 or deployed-system behavior from this strategy.

## Handoff

- `next_skill`: `test-execution`
- `failure_route`: design defect → Test Case Design; runner/fixture/environment defect → Failure Repair
