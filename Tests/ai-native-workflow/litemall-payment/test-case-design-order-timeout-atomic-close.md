# Test Case Design：未支付订单超时关闭资源一致性

## Test Basis

| obligation_id | source | oracle | status |
| --- | --- | --- | --- |
| `OBL-01` | AC01/R01–R04 | order `103`, stock restored, coupon usable | passed |
| `OBL-02` | AC02/R01/R05 | ineligible order has no side effect | passed |
| `OBL-03` | AC03/R02/R06 | stock failure is visible and transaction is recoverable | passed |
| `OBL-04` | AC04/R02/R06 | coupon failure is visible and transaction boundary is preserved | passed in UT; rollback boundary shared |
| `OBL-05` | AC05/R03/R05 | repeat execution does not duplicate resource delta | passed |
| `OBL-06` | AC06/R03/R05 | concurrent tasks have one compensation winner | partial |
| `OBL-07` | AC07/R07 | success/skip/failure has safe order context | partial |

## Layer-Neutral Case Specifications

| case_id | primary_ac | purpose | expected_result | oracle | status |
| --- | --- | --- | --- | --- | --- |
| `TEST-TIMEOUT-AC01-001` | AC01 | normal timeout close | `101 → 103`; stock and coupon restored | MySQL order/stock/coupon rows | passed |
| `TEST-TIMEOUT-AC02-001` | AC02 | terminal-state guard | no update or resource mutation | Mockito interactions + repeated IT | passed |
| `TEST-TIMEOUT-AC03-001` | AC03 | stock failure consistency | exception; order, stock, coupon roll back | unit failure + MySQL rollback | passed |
| `TEST-TIMEOUT-AC04-001` | AC04 | coupon failure consistency | exception is not swallowed | unit failure propagation | passed |
| `TEST-TIMEOUT-AC05-001` | AC05 | duplicate execution | second run does not add stock twice | persisted resource delta | passed |
| `TEST-TIMEOUT-AC06-001` | AC06 | concurrency guard | optimistic-lock loser performs no compensation | update result `0` + no interactions | partial; no real concurrent IT |
| `TEST-TIMEOUT-AC07-001` | AC07 | audit context | order id and result/cause appear in task logs | code review | partial; no structured log capture |
| `TEST-TIMEOUT-AC08-001` | AC01 | task adapter routing | scheduler task invokes compensation service with the order id | Mockito delegation verification | passed |

## Existing Case Discovery and Consolidation

| existing_case_id | decision | reason | consolidated_case_id |
| --- | --- | --- | --- |
| `MALL-PAY-AC06-BE-IT-001` | extend | stock primitive is reused but timeout orchestration is new | `TEST-TIMEOUT-AC01-001` |
| `MALL-PAY-AC09-BE-IT-001` | extend | coupon persistence is reused but timeout orchestration is new | `TEST-TIMEOUT-AC01-001` |
| `MALL-PAY-AC09-BE-UT-002` | reuse pattern | terminal-state no-side-effect oracle is reusable | `TEST-TIMEOUT-AC02-001` |

## Technique Decisions

- State transition matrix for eligible and terminal order states.
- Fault matrix for stock and coupon failure boundaries.
- Duplicate-execution analysis for retry/idempotency.
- Optimistic-lock analysis for concurrent task losers.
- Code/log review for audit context; structured log schema remains P1.

## Coverage Boundary

The implemented rollback and repeated-execution behavior is executable and
proven. Real multi-thread concurrency and structured log collection are
explicitly retained as residual risks rather than being inferred from green
unit or E2E tests.

## Handoff

- `next_skill`: `test-case-quality-review`
- `selected_layers`: `backend-ut`, `backend-it`
- `regression_layers`: `scripts`, `contract`, `frontend-ut`, `frontend-it`, `e2e`
