# Test Execution Report: Cancel Unpaid Order

- Feature: `MALL-ORD-CANCEL-001`
- Strategy: `test-strategy-order-cancel-unpaid.md`
- Profile: `targeted-medium`
- Execution date: 2026-08-10
- Overall status: **pass with environment-blocked backend IT and deferred AC06**

## Executed lanes

| Layer | Command | Result | Evidence |
| --- | --- | --- | --- |
| Fast gate | `./Tests/scripts/run-layered-tests.sh fast` | PASS | Backend UT, frontend UT, frontend IT, contract and performance-gate checks completed; coverage reports generated |
| Backend UT | `./Tests/scripts/run-layered-tests.sh backend-ut` | PASS | Cancel success and paid-order guard cases passed; expected negative pay-notify exception is handled by the test |
| Contract | `./Tests/scripts/run-layered-tests.sh contract` | PASS | `/order/cancel` method and `{orderId}` request contract passed |
| Frontend IT | `./Tests/scripts/run-layered-tests.sh frontend-it` | PASS | 2 files, 15 tests passed |
| Backend IT | `./Tests/scripts/run-layered-tests.sh backend-it` | BLOCKED | Testcontainers could not find Docker; Colima is not running |
| E2E | `./Tests/scripts/run-layered-tests.sh e2e` | PASS | 4 tests passed, including `MALL-PAY-AC09-E2E-001` |

## Acceptance evidence

- AC01: covered by backend UT, contract, frontend IT, and E2E.
- AC02: covered by backend UT and E2E; the E2E flow verifies stock recovery through the local harness.
- AC03: covered by backend UT and E2E; coupon recovery is asserted in the selected cancel flow.
- AC04: paid-order guard covered by backend UT. Wrong-owner and malformed-body variants remain residual gaps.
- AC05: contract and frontend IT passed for `POST /order/cancel` with `orderId`.
- AC06: not proven. The selected suite has no failure-injection case for stock/coupon failure and transaction rollback.

## Environment block and rerun

Backend IT is an execution-environment block, not a product failure. The runner
reported that `unix:///Users/summer/.colima/default/docker.sock` is not
listening and no valid Docker environment was available.

After Docker/Colima is available, rerun:

```bash
cd /Users/summer/Work/Code/AI/training/litemall
./Tests/scripts/run-layered-tests.sh backend-it
```

The deferred AC06 case should be implemented with controlled failure injection
before claiming full transaction rollback coverage.

## Coverage boundary

The generated JaCoCo and frontend coverage reports are useful implementation
signals, but they do not replace behavior evidence for AC06 or the missing
wrong-owner/malformed-request variants.
