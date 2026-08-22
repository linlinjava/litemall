# Quality Review：取消未支付订单

## Test Case Quality Review

- `design_id`: `MALL-ORD-CANCEL-001-DESIGN`
- `overall_score`: 88
- `decision`: `pass-with-residual-risk`
- `traceability`: pass
- `oracle_quality`: pass for AC01–AC05; AC06 blocked explicitly
- `existing_case_consolidation`: pass
- `layer_fit`: pass
- `benchmark_risk`: watch

## Findings

| severity | case/AC | issue | recommendation |
| --- | --- | --- | --- |
| medium | AC06 | no real failure-injection test proves rollback across order, stock, and coupon | keep out of current gate; add Backend IT when failure injection and product policy are confirmed |
| low | AC04 | current tests cover paid-order guard but not malformed body or wrong-user ownership | add focused Backend UT/API cases in a follow-up |
| low | AC05 | contract checks mapping and request shape, not complete response schema | retain as boundary evidence; do not claim complete schema coverage |
| low | AC01–AC03 | E2E uses local harness | retain as browser wiring evidence, not deployed-system evidence |

## Existing Case Decisions

| existing_case_id | decision | reason |
| --- | --- | --- |
| `MALL-PAY-AC09-BE-UT-001` | reuse | directly proves status, stock callback, and coupon release behavior |
| `MALL-PAY-AC09-BE-UT-002` | reuse | proves paid order cannot enter cancellation path |
| `MALL-PAY-AC09-BE-IT-001` | extend evidence | proves persistence primitives, not complete cancel orchestration |
| `MALL-PAY-AC00-CONTRACT-001` | extend evidence | proves POST mapping and contract ID alignment |
| `MALL-PAY-AC09-FE-IT-001` | reuse | exact frontend request payload is asserted |
| `MALL-PAY-AC09-E2E-001` | reuse | critical local browser path already exists |

## Strategy Handoff

- `next_skill`: `test-strategy`
- `selected_case_ids`:
  - `MALL-PAY-AC09-BE-UT-001`
  - `MALL-PAY-AC09-BE-UT-002`
  - `MALL-PAY-AC09-BE-IT-001`
  - `MALL-PAY-AC00-CONTRACT-001`
  - `MALL-PAY-AC09-FE-IT-001`
  - `MALL-PAY-AC09-E2E-001`
- `selected_evidence_layers`: `backend-ut`, `contract`, `frontend-it`, `backend-it`, `e2e`
- `must_keep_tests`: AC01–AC05 P0/P1 cases
- `deferred_cases`: `TEST-ORD-CANCEL-AC06-001`
- `residual_risk`: transaction rollback under injected inventory/coupon failure; malformed body and wrong-user guard coverage
